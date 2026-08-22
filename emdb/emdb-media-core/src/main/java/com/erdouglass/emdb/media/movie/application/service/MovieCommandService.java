package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.Result;
import com.erdouglass.emdb.media.Result.Status;
import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.DeleteMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieAuditRepository;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.domain.Movie;
import com.erdouglass.emdb.media.movie.domain.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.MovieId;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;
import com.erdouglass.emdb.media.movie.domain.exception.MovieNotFoundException;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

/// Orchestrates every write to the movie catalogue.
///
/// Implements all four write ports together because they share one recurring
/// shape — load, check, mutate, persist, audit — and splitting them would
/// duplicate that sequence four times. The rules themselves live on the
/// aggregate; this class only sequences them and owns the transaction.
///
/// Auditing is not optional and not a listener: each method appends the changes
/// it caused inside the same transaction, so a write cannot commit without its
/// trail.
///
/// Surrogate ids are generated here as time-ordered UUIDv7 values, giving a
/// movie identity before it reaches the database.
///
/// Package-private and stateless beyond its injected collaborators; the CDI
/// container hands it out through the port interfaces.
@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase, UpdateMovieUseCase, DeleteMovieUseCase {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  private static final Logger LOGGER = Logger.getLogger(MovieCommandService.class);
  
  @Inject
  MovieAuditRepository audit;
  
  @Inject
  MovieCommandRepository movies;

  /// Ingests a title, inserting it if the natural id is new.
  ///
  /// The lookup by TMDB id is what makes this an upsert. No version is checked,
  /// so an ingestion run never fails as stale — the trade is that a concurrent
  /// edit can be overwritten, which is why interactive clients use
  /// [#update(UpdateMovieCommand)] instead.
  ///
  /// @param command the complete intended state of the title
  /// @return the catalogue id, the version afterwards, and which outcome occurred
  /// @throws LockedMovieException if the title exists and is locked, including
  ///         when the incoming details are identical
  @Override
  @Transactional
  public Result save(SaveMovieCommand command) {
    var details = MovieMapper.toMovieDetails(command);
    return movies.findBySourceId(command.sourceId())
        .map(existing -> update(existing, details))
        .orElseGet(() -> insert(command.sourceId(), details));
  }
  
  /// Edits an existing title, refusing the write if the caller's version is
  /// stale.
  ///
  /// The version is checked before the details are applied, so a stale request
  /// leaves the aggregate untouched and writes no audit rows.
  ///
  /// @param command the intended state, target id and the version the caller read
  /// @return the catalogue id, the version afterwards, and which outcome occurred
  /// @throws MovieNotFoundException if no title carries the command's id
  /// @throws StalePersonException if the stored version differs from the supplied
  ///         one
  /// @throws IllegalArgumentException if the command's id is malformed
  @Override
  @Transactional
  public Result update(UpdateMovieCommand command) {
    var details = MovieMapper.toMovieDetails(command);
    Movie existing = movies.findByPublicId(MoviePublicId.of(command.publicId()))
        .orElseThrow(() -> new MovieNotFoundException(command.publicId()));
    existing.checkVersion(Version.of(command.version()));
    return update(existing, details);
  }
  
  /// Removes a title, closing out its history first.
  ///
  /// The load is what makes the audit possible: the title has to be read before
  /// it can be described as removed. Both writes share the transaction, so
  /// history cannot be recorded for a delete that then rolls back.
  ///
  /// @param id the catalogue id of the title to remove
  /// @throws MovieNotFoundException if no title carries `id`
  @Override
  @Transactional
  public void delete(MoviePublicId id) {
    Movie movie = movies.findByPublicId(id)
        .orElseThrow(() -> new MovieNotFoundException(id.value())); 
    audit.append(movie.id(), movie.publicId().orElseThrow(), movie.changesAsDeleted());
    movies.deleteByPublicId(id);
    LOGGER.infof("Deleted: %s", movie);
  }
  
  /// Creates and persists a title that the catalogue has not seen before.
  ///
  /// The audit trail is seeded from the *persisted* aggregate rather than the
  /// one just built, because only the persisted one knows the catalogue id the
  /// database assigned.
  ///
  /// @param sourceId the natural id of the new title
  /// @param details the initial details
  /// @return the catalogue id and initial version, reported as a creation
  private Result insert(SourceId sourceId, MovieDetails details) {
    var movie = Movie.create(MovieId.of(GENERATOR.generate()), sourceId, details);
    var inserted = movies.insert(movie);
    audit.append(inserted.id(), inserted.publicId().orElseThrow(), inserted.changesAsAdded());
    LOGGER.infof("Created: %s", inserted);
    return Result.of(
        inserted.publicId().map(MoviePublicId::value).orElseThrow(), 
        inserted.version().map(Version::value).orElseThrow(), 
        Status.CREATED);    
  }
  
  /// Applies details to a loaded title and persists only if something moved.
  ///
  /// Shared by both write paths, which is why it takes an already-loaded
  /// aggregate: each caller has its own way of finding the title and its own
  /// pre-checks, and only the part after that is common.
  ///
  /// The early return on an empty diff is what makes ingestion cheap to replay —
  /// a no-op run touches neither the movie table nor the audit table, and does
  /// not advance the version.
  ///
  /// @param movie the loaded aggregate, already version-checked if the caller
  ///        requires it
  /// @param target the complete intended details
  /// @return the catalogue id and version, reported as unchanged when the
  ///         details already matched
  private Result update(Movie movie, MovieDetails target) {
    var changes = movie.update(target);
    if (changes.isEmpty()) {
      return Result.of(
          movie.publicId().map(MoviePublicId::value).orElseThrow(), 
          movie.version().map(Version::value).orElseThrow(), 
          Status.UNCHANGED);
    }
    var updated = movies.update(movie);
    audit.append(updated.id(), updated.publicId().orElseThrow(), changes);
    LOGGER.infof("Updated: %s", updated);
    return Result.of(
        updated.publicId().map(MoviePublicId::value).orElseThrow(), 
        updated.version().map(Version::value).orElseThrow(), 
        Status.UPDATED);    
  }
}
