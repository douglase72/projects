package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.AggregateRoot;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.SurrogateId;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.domain.command.RehydrateMovieCommand;
import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.domain.command.UpdateMovieCommand;
import com.erdouglass.emdb.media.movie.domain.exception.StaleMovieException;

public final class Movie extends AggregateRoot {
  private Title title;
  private ReleaseDate releaseDate;
  private Score score;
  private LanguageCode originalLanguage;
  private Overview overview;
  
  private Movie(
      PublicId id, 
      SurrogateId surrogateId, 
      TmdbId tmdbId,
      Version version,
      Title title,
      ReleaseDate releaseDate,
      Score score,
      LanguageCode originalLanguage,
      Overview overview) {
    super(id, surrogateId, tmdbId, version);
    this.title = Objects.requireNonNull(title, "movie title is required");
    this.releaseDate = releaseDate;
    this.score = score;
    this.originalLanguage = originalLanguage;
    this.overview = overview;
  }
  
  public static Movie create(SaveMovieCommand command) {
    return new Movie(
        PublicId.newId(), 
        null, 
        command.tmdbId(),
        Version.of(0L),
        command.title(),
        command.releaseDate(),
        command.score(),
        command.originalLanguage(),
        command.overview());
  }
  
  public static Movie rehydrate(RehydrateMovieCommand command) {
    return new Movie(
        command.id(), 
        command.surrogateId(), 
        command.tmdbId(),
        command.version(),
        command.title(),
        command.releaseDate(),
        command.score(),
        command.originalLanguage(),
        command.overview());
  }
  
  public void update(UpdateMovieCommand command) {
    if (command.version() == null || !command.version().equals(version())) {
      throw new StaleMovieException(version().value().toString());
    }
    this.title = command.title();
    this.releaseDate = command.releaseDate();
    this.score = command.score();
    this.originalLanguage = command.originalLanguage();
    this.overview = command.overview();
  }
  
  public Title title() { return title; }
  public Optional<ReleaseDate> releaseDate() { return Optional.ofNullable(releaseDate); }
  public Optional<Score> score() { return Optional.ofNullable(score); }
  public Optional<LanguageCode> originalLanguage() { return Optional.ofNullable(originalLanguage); }
  public Optional<Overview> overview() { return Optional.ofNullable(overview); }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id().value() 
        + ", surrogateId=" + surrogateId().map(SurrogateId::value).orElse(null)
        + ", tmdbId=" + tmdbId().value()
        + ", version=" + version().value()
        + ", title=" + title().value()
        + ", releaseDate=" + releaseDate().map(ReleaseDate::toLocalDate).orElse(null)
        + "]";
  }
}
