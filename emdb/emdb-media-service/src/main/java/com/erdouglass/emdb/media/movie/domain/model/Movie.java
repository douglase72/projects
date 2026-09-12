package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.kernel.AggregateRoot;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.kernel.Version;

public final class Movie extends AggregateRoot {
  private MovieDetails details;
  
  private Movie(PublicId id, TmdbId tmdbId, Version version, MovieDetails details) {
    super(id, tmdbId, version);
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public static Movie create(TmdbId tmdbId, MovieDetails details) {
    var movie = new Movie(PublicId.newId(), tmdbId, Version.of(0L), details);
    return movie;
  }
  
  public Title title() { return details.title(); }
  public Optional<ReleaseDate> releaseDate() { return Optional.ofNullable(details.releaseDate()); }
  public Optional<Score> score() { return Optional.ofNullable(details.score()); }
  public Optional<LanguageCode> originalLanguage() { return Optional.ofNullable(details.originalLanguage()); }
  public Optional<Overview> overview() { return Optional.ofNullable(details.overview()); }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id().value() 
        + ", tmdbId=" + tmdbId().value()
        + ", version=" + version().value()
        + ", title=" + title().value()
        + ", releaseDate=" + releaseDate().map(ReleaseDate::toLocalDate).orElse(null)
        + "]";
  }
}
