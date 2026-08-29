package com.erdouglass.emdb.media.movie.domain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.kernel.Version;

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
/// * [TmdbId] — natural id supplied by the upstream catalogue, present from
///   creation and never reassigned.
public final class Movie {
  private final MovieId id;
  private final MoviePublicId publicId;
  private final TmdbId tmdbId;
  private final Version version;
  
  private MovieDetails details;
  private final List<MovieCredit> credits;
  
  private Movie(
      MovieId id, 
      MoviePublicId publicId, 
      TmdbId tmdbId, 
      Version version, 
      MovieDetails details, 
      List<MovieCredit> credits) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.publicId = publicId;
    this.tmdbId = Objects.requireNonNull(tmdbId, "TMDB id is required");
    this.version = Objects.requireNonNull(version, "version is required");
    this.details = Objects.requireNonNull(details, "details are required");
    this.credits = new ArrayList<>(credits);
  }
  
  public static Movie create(TmdbId tmdbId, MovieDetails details, List<CreditDetails> credits) {
    Movie movie = new Movie(MovieId.newId(), null, tmdbId, Version.of(0L), details, List.of());
    movie.syncCredits(credits);
    return movie;
  }
  
  public static Movie rehydrate(
      MovieId id, 
      MoviePublicId publicId, 
      TmdbId tmdbId, 
      Version version, 
      MovieDetails details, 
      List<MovieCredit> credits) {
    return new Movie(id, publicId, tmdbId, version, details, credits);
  }
  
  public void update(MovieDetails details, List<CreditDetails> credits) {
    this.details = Objects.requireNonNull(details, "details are required");
    syncCredits(credits);
  }  
  
  public MovieId id() { return id; } 
  public Optional<MoviePublicId> publicId() { return Optional.ofNullable(publicId); }
  public TmdbId tmdbId() { return tmdbId; }
  public Version version() { return version; }
  public Title title() { return details.title(); }
  public Optional<ReleaseDate> releaseDate() { return Optional.ofNullable(details.releaseDate()); }
  public Optional<Score> score() { return Optional.ofNullable(details.score()); }
  public Optional<LanguageCode> originalLanguage() { return Optional.ofNullable(details.originalLanguage()); }
  public Optional<Overview> overview() { return Optional.ofNullable(details.overview()); }
  public MovieDetails details() { return details; }
  public List<MovieCredit> credits() { return List.copyOf(credits); }
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
  
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
        + ", tmdbId=" + tmdbId.value()
        + ", version=" + version.value()
        + ", title=" + title().value()
        + ", releaseDate=" + releaseDate().map(ReleaseDate::toLocalDate).orElse(null)
        + ", score=" + score().map(Score::value).orElse(null)        
        + "]";
  }
  
  private void syncCredits(List<CreditDetails> incoming) {
    Map<TmdbCreditId, MovieCredit> existing = credits.stream()
        .collect(Collectors.toMap(MovieCredit::tmdbId, Function.identity()));
    var seen = new HashSet<TmdbCreditId>();
    
    for (var credit : incoming) {
      if (!seen.add(credit.tmdbId())) {
        throw new IllegalArgumentException("duplicate credit: " + credit);
      }
      var match = existing.get(credit.tmdbId());
      if (match == null) {
        switch (credit) {
          case CastDetails d -> credits.add(CastCredit.create(d));
          case CrewDetails d -> credits.add(CrewCredit.create(d));
        }
      } else {
        match.update(credit);
      }
    }
    credits.removeIf(c -> !seen.contains(c.tmdbId()));
  }
}
