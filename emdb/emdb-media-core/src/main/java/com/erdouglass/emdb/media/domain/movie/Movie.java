package com.erdouglass.emdb.media.domain.movie;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.domain.exception.StaleMovieException;
import com.erdouglass.emdb.media.domain.shared.Score;
import com.erdouglass.emdb.media.domain.shared.Version;

/// Movie aggregate 
/// 
/// Movie aggregate carries three identifiers:
/// * [MovieId] - surrogate id assigned by the application when the aggregate is 
///   created. This serves as the Java identity and is never exposed to the public.
/// * [MoviePublicId] - public facing id assigned by the database when the 
///   aggregate is first persisted.
/// * [TmdbId] - natural id assigned by the application.
public final class Movie {
  private final MovieId id;
  private final MoviePublicId publicId;
  private final TmdbId tmdbId;
  private final Version version;
  
  private MovieDetails details;
  
  private Movie(
      MovieId id, 
      MoviePublicId publicId, 
      TmdbId tmdbId, 
      MovieDetails details, 
      Version version) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.publicId = publicId;
    this.tmdbId = Objects.requireNonNull(tmdbId, "tmdbId is required");
    this.details = Objects.requireNonNull(details, "details are required");
    this.version = version;
  }
  
  /// Create a new movie aggregate.
  /// 
  /// The movie public id and version will be null until its persisted in the 
  /// database.
  public static Movie create(MovieId id, TmdbId tmdbId, MovieDetails details) {
    return new Movie(id, null, tmdbId, details, null);
  }
  
  /// Create a complete movie aggregate.
  /// 
  /// This only gets called by the MovieCommandAdapter.
  public static Movie rehydrate(
      MovieId id, 
      MoviePublicId publicId, 
      TmdbId tmdbId, 
      MovieDetails details, 
      Version version) {
    return new Movie(id, publicId, tmdbId, details, version);
  }
  
  /// Update movie details.
  public List<FieldChange> update(MovieDetails targetDetails) {
    Objects.requireNonNull(targetDetails, "details are required");  
    var changes = MovieField.diff(this.details, targetDetails);
    this.details = targetDetails;
    return changes;
  }
  
  public List<FieldChange> changesAsAdded() {
    return MovieField.diff(null, details);
  }
  
  public List<FieldChange> changesAsDeleted() {
    return MovieField.diff(details, null);
  }
  
  public void checkVersion(Version expected) {
    if (version == null || !version.equals(expected)) {
      throw new StaleMovieException(expected.value().toString());
    }
  }
  
  public MovieDetails details() {
    return details;
  }
  
  public MovieId id() {
    return id;
  }
  
  public Optional<MoviePublicId> publicId() {
    return Optional.ofNullable(publicId);
  }
  
  public TmdbId tmdbId() {
    return tmdbId;
  }
  
  public Optional<Version> version() {
    return Optional.ofNullable(version);
  }
  
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
        + ", tmdbId=" + tmdbId().value()
        + ", version=" + version().map(Version::value).orElse(null)
        + ", title=" + details.title().value()
        + ", releaseDate=" + details.releaseDate().map(ReleaseDate::toLocalDate).orElse(null)
        + ", score=" + details.score().map(Score::value).orElse(null)
        + "]";
  }  
}
