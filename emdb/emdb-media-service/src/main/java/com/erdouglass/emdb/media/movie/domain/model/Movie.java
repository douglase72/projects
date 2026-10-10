package com.erdouglass.emdb.media.movie.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.media.movie.domain.event.MovieEvent;
import com.erdouglass.emdb.media.movie.domain.event.MovieSaved;
import com.erdouglass.emdb.media.shared.domain.model.AggregateRoot;
import com.erdouglass.emdb.media.shared.domain.model.LanguageCode;
import com.erdouglass.emdb.media.shared.domain.model.MediaId;
import com.erdouglass.emdb.media.shared.domain.model.Overview;
import com.erdouglass.emdb.media.shared.domain.model.Score;
import com.erdouglass.emdb.media.shared.domain.model.Title;
import com.erdouglass.emdb.media.shared.domain.model.Version;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public final class Movie extends AggregateRoot {
  private MovieDetails details;
  private final List<MovieEvent> events = new ArrayList<>();
  
  private Movie(MediaId id, TmdbId tmdbId, Version version, MovieDetails details) {
    super(id, tmdbId, version);
    this.details = Objects.requireNonNull(details, "details must not be null");
  }
  
  public static Movie create(CorrelationId correlationId, TmdbId tmdbId, MovieDetails details) {
    var movie = new Movie(MediaId.newId(), tmdbId, Version.of(0L), details);
    movie.raise(MovieSaved.builder()
        .id(MessageId.newId())
        .correlationId(correlationId)
        .mediaId(movie.id())
        .tmdbId(tmdbId)
        .build());
    return movie;
  }
  
  public List<MovieEvent> pullEvents() {
    var pulledEvents = List.copyOf(events);
    events.clear();
    return pulledEvents;
  }
  
  public Title title() { return details.title(); }
  public Optional<ReleaseDate> releaseDate() { return Optional.ofNullable(details.releaseDate()); }
  public Optional<Score> score() { return Optional.ofNullable(details.score()); }
  public Optional<LanguageCode> originalLanguage() { return Optional.ofNullable(details.originalLanguage()); }
  public Optional<Overview> overview() { return Optional.ofNullable(details.overview()); }

  private void raise(MovieEvent event) {
    events.add(event);
  }
}
