package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.shared.domain.model.AggregateRoot;
import com.erdouglass.emdb.media.shared.domain.model.LanguageCode;
import com.erdouglass.emdb.media.shared.domain.model.Overview;
import com.erdouglass.emdb.media.shared.domain.model.PublicId;
import com.erdouglass.emdb.media.shared.domain.model.Score;
import com.erdouglass.emdb.media.shared.domain.model.Title;
import com.erdouglass.emdb.media.shared.domain.model.Version;
import com.erdouglass.emdb.shared.kernel.TmdbId;

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
  
  public void update(MovieDetails details) {
    this.details = details;
  }
  
  public static Movie rehydrate(PublicId id, TmdbId tmdbId, Version version, MovieDetails details) {
    return new Movie(id, tmdbId, version, details);
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
