package com.erdouglass.emdb.media.movie.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.erdouglass.emdb.media.kernel.SourceId;
import com.erdouglass.emdb.media.kernel.SourceId.Source;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.domain.command.MovieMapper;
import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.domain.exception.StaleMovieException;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

/// Movie aggregate.
///
/// A movie carries three identifiers, each with a different lifetime and a
/// different audience:
///
/// * [MovieId] — surrogate id assigned by the application when the aggregate is
///   created. This is the Java identity used by [#equals(Object)] and
///   [#hashCode()], and it is never exposed to the public.
/// * [MoviePublicId] — public-facing id derived from the key the database
///   assigns on first insert. Absent until the aggregate has been persisted.
/// * [SourceId] — natural id supplied by the upstream catalogue, present from
///   creation and never reassigned.
///
/// State changes go through [#update(MovieDetails)], which returns the list of
/// field-level differences so the caller can audit them; the aggregate does not
/// write to the audit trail itself. [#version()] is likewise read-only here —
/// it is assigned by the persistence layer, and [#checkVersion(Version)] only
/// compares it.
///
/// A locked movie rejects detail changes but still accepts [#lock(boolean)],
/// which is how a lock is lifted.
///
/// Not thread-safe: [#details] and [#locked] are mutable and unsynchronised.
/// Instances are expected to be confined to a single transaction.
public final class Movie {
  private static final TimeBasedEpochGenerator ID_GENERATOR = Generators.timeBasedEpochGenerator();
  
  private final MovieId id;
  private final MoviePublicId publicId;
  private final SourceId sourceId;
  private final Version version;
  private final List<MovieCredit> credits = new ArrayList<>();
  
  private MovieDetails details;
  
  private Movie(
      MovieId id, 
      MoviePublicId publicId, 
      SourceId sourceId, 
      MovieDetails details,
      Version version) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.publicId = publicId;
    this.sourceId = Objects.requireNonNull(sourceId, "sourceId is required");
    this.details = Objects.requireNonNull(details, "details are required");
    this.version = Objects.requireNonNull(version, "version is required");
  }
  
  public static Movie create(SaveMovieCommand command) {
    var sourceId = command.sourceId();;
    var details = MovieMapper.toMovieDetails(command);
    Movie movie = new Movie(MovieId.of(ID_GENERATOR.generate()), null, sourceId, details, Version.of(0L));
    movie.updateCredits(command);
    return movie;
  }
  
  /// Reconstructs a persisted movie from stored state.
  ///
  /// @param dto a persisted movie
  public static Movie rehydrate(      
      MovieId id, 
      MoviePublicId publicId, 
      SourceId sourceId, 
      MovieDetails details,
      Version version) {
    Objects.requireNonNull(publicId, "public id is required");
    Movie movie = new Movie(id, publicId, sourceId, details, version);
    return movie;
  }
  
  public List<MovieFieldChange> update(SaveMovieCommand command) {
    updateCredits(command);
    var details = MovieMapper.toMovieDetails(command);
    var changes = MovieField.diff(this.details, details);
    if (changes.isEmpty()) {
      return changes;
    }
    this.details = details;
    return changes;
  }
  
  /// Renders the current details as if every populated field had just been added.
  ///
  /// Used to seed the audit trail on insert, so that a title's history starts
  /// with a complete picture rather than with nothing.
  ///
  /// @return one addition per populated field, in declaration order
  public List<MovieFieldChange> changesAsAdded() { return MovieField.diff(null, details); }  
  
  /// Renders the current details as if every populated field had just been
  /// removed.
  ///
  /// Used to close out the audit trail before a delete, so the history records
  /// what was lost rather than ending abruptly.
  ///
  /// @return one removal per populated field, in declaration order
  public List<MovieFieldChange> changesAsDeleted() { return MovieField.diff(details, null); }
  
  /// Asserts that the caller's view of this movie is current.
  ///
  /// Call before mutating on behalf of a client that read the movie earlier; a
  /// mismatch means someone else wrote in between and the caller's edit would
  /// silently discard that write.
  ///
  /// An unpersisted movie has no version and therefore never matches.
  ///
  /// @param expected the version the caller believes it holds
  /// @throws StaleMovieException if this movie has no version, or its version
  ///         differs from `expected`
  /// @throws NullPointerException if `expected` is `null`
  public void checkVersion(Version expected) {
    Objects.requireNonNull(expected, "expected version is required");
    if (version == null || !version.equals(expected)) {
      throw new StaleMovieException("movie %s has invalid version: %d"
          .formatted(publicId.value(), version.value()));
    }
  }
  
  // Accessors
  public List<MovieCredit> credits() { return List.copyOf(credits); }
  public MovieDetails details() { return details; }  
  public MovieId id() { return id; }  
  public Optional<MoviePublicId> publicId() { return Optional.ofNullable(publicId); }
  public SourceId sourceId() { return sourceId; }
  public Version version() { return version; }
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  /// Compares by surrogate id only.
  ///
  /// Two instances describing the same title are equal even if their details,
  /// lock state or version differ — identity is the aggregate's, not the
  /// snapshot's. Use [#details()] to compare content.
  ///
  /// @param obj the object to compare with
  /// @return `true` if `obj` is a movie with the same [#id()]
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Movie other = (Movie) obj;
    return Objects.equals(id, other.id);
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id.value()
        + ", publicId=" + publicId().map(MoviePublicId::value).orElse(null)
        + ", source=" + sourceId().provider()
        + ", sourceId=" + sourceId().id()
        + ", version=" + version().value()
        + ", title=" + details.title().value()
        + ", releaseDate=" + details.releaseDate().value().toDateString()
        + ", score=" + details.score().value()
        + "]";
  }
  
  /// Update the movie credits.
  /// 
  /// The identity of any existing movie credits that are also present in the 
  /// list of incoming credits must be preserved.
  private void updateCredits(SaveMovieCommand command) {
    Map<SourceId, MovieCredit> existing = credits.stream()
        .collect(Collectors.toMap(MovieCredit::sourceId, Function.identity()));
    List<MovieCredit> creditsToSave = new ArrayList<>();
    
    for (var credit : command.cast()) {
      var sourceId = SourceId.of(Source.from(credit.source()), credit.id());
      if (existing.get(command.sourceId()) instanceof CastCredit c) {
        creditsToSave.add(c.update(sourceId, credit));
      } else {
        creditsToSave.add(CastCredit.create(CreditId.of(ID_GENERATOR.generate()), sourceId, credit));
      }
    }
    
    for (var credit : command.crew()) {
      var sourceId = SourceId.of(Source.from(credit.source()), credit.id());
      if (existing.get(sourceId) instanceof CrewCredit c) {
        creditsToSave.add(c.update(sourceId, credit));
      } else {
        creditsToSave.add(CrewCredit.create(CreditId.of(ID_GENERATOR.generate()), sourceId, credit));
      }
    }
    credits.clear();
    credits.addAll(creditsToSave);
  }
}
