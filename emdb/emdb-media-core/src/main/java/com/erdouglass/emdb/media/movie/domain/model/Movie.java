package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.kernel.Version;

/// Movie aggregate.
///
/// A movie carries two identifiers, each with a different lifetime and a
/// different audience:
///
/// * [MovieId] — surrogate id assigned by the application when the aggregate is
///   created. This is the Java identity used by [#equals(Object)] and
///   [#hashCode()], and it is never exposed to the public.
/// * [TmdbId] — natural id supplied by the upstream catalogue, present from
///   creation and never reassigned.
public final class Movie {
  private final MovieId id;
  private final TmdbId tmdbId;
  private final Version version;
  
  private MovieDetails details;
  
  private Movie(
      MovieId id, 
      TmdbId tmdbId, 
      Version version, 
      MovieDetails details) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.tmdbId = Objects.requireNonNull(tmdbId, "TMDB id is required");
    this.version = Objects.requireNonNull(version, "version is required");
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public static Movie create(TmdbId tmdbId, MovieDetails details) {
    Movie movie = new Movie(MovieId.newId(), tmdbId, Version.of(0L), details);
    return movie;
  }
  
  public static Movie rehydrate(
      MovieId id, 
      TmdbId tmdbId, 
      Version version, 
      MovieDetails details) {
    return new Movie(id, tmdbId, version, details);
  }
  
  public void update(MovieDetails details) {
    this.details = Objects.requireNonNull(details, "details are required");
  }  
  
  public MovieId id() { return id; } 
  public TmdbId tmdbId() { return tmdbId; }
  public Version version() { return version; }
  public Title title() { return details.title(); }
  public Optional<ReleaseDate> releaseDate() { return Optional.ofNullable(details.releaseDate()); }
  public Optional<Score> score() { return Optional.ofNullable(details.score()); }
  public Optional<LanguageCode> originalLanguage() { return Optional.ofNullable(details.originalLanguage()); }
  public Optional<Overview> overview() { return Optional.ofNullable(details.overview()); }
  public MovieDetails details() { return details; }
  
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
        + ", tmdbId=" + tmdbId.value()
        + ", version=" + version.value()
        + ", title=" + title().value()
        + ", releaseDate=" + releaseDate().map(ReleaseDate::toLocalDate).orElse(null)
        + ", score=" + score().map(Score::value).orElse(null)        
        + "]";
  }
}
