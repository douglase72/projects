package com.erdouglass.emdb.media.movie.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.AggregateRoot;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.domain.event.MovieCreated;
import com.erdouglass.emdb.media.movie.domain.event.MovieEvent;
import com.erdouglass.emdb.media.movie.domain.event.MovieUpdated;

public final class Movie extends AggregateRoot {
  private MovieDetails details;
  private final List<MovieEvent> domainEvents = new ArrayList<>();
  
  private Movie(PublicId id, TmdbId tmdbId, Version version, MovieDetails details) {
    super(id, tmdbId, version);
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public static Movie create(TmdbId tmdbId, MovieDetails details) {
    var movie = new Movie(PublicId.newId(), tmdbId, Version.of(0L), details);
    movie.raise(MovieCreated.of(movie.id(), movie.tmdbId(), movie.title()));
    return movie;
  }
  
  public static Movie rehydrate(PublicId id, TmdbId tmdbId, Version version, MovieDetails details) {
    return new Movie(id, tmdbId, version, details);
  }
  
  public void update(MovieDetails details) {
    this.details = details;
    raise(MovieUpdated.of(id(), tmdbId(), title()));
  }
  
  public List<MovieEvent> pullEvents() {
    var events = List.copyOf(domainEvents);
    domainEvents.clear();
    return events;
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
  
  private void raise(MovieEvent event) {
    domainEvents.add(event);
  }
}
