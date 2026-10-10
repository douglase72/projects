package com.erdouglass.emdb.scraper.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.scraper.domain.event.MovieEvent;
import com.erdouglass.emdb.scraper.domain.event.MovieExtracted;
import com.erdouglass.emdb.scraper.domain.event.MovieStarted;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public final class Movie {
  private final CorrelationId correlationId;
  private final List<MovieEvent> events = new ArrayList<>();
  
  private MovieDetails details;

  private Movie(CorrelationId correlationId) {
    this.correlationId = correlationId;
  }
  
  public static Movie create(CorrelationId correlationId, TmdbId tmdbId) {
    var movie = new Movie(correlationId);
    movie.raise(MovieStarted.of(MessageId.newId(), correlationId, tmdbId));
    return movie;
  }
  
  public void extract(MovieDetails details) {
    this.details = details;
    events.add(MovieExtracted.of(MessageId.newId(), correlationId, details));
  }
  
  public List<MovieEvent> pullEvents() {
    List<MovieEvent> pulledEvents = List.copyOf(events);
    events.clear();
    return pulledEvents;
  }
  
  public TmdbId tmdbId() { return details.tmdbId(); }
  public String title() { return details.title(); }
  public String releaseDate() { return details.releaseDate(); }
  public BigDecimal score() { return details.score(); }
  public String originalLanguage() { return details.originalLanguage(); }
  public String overview() { return details.overview(); }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[correlationId=" + correlationId
        + ", tmdbId=" + tmdbId()
        + ", releaseDate=" + releaseDate()
        + ", score=" + score()
        + ", originalLanguage=" + originalLanguage()
        + ", overview=" + overview()
        + "]";
  }
  
  private void raise(MovieEvent event) {
    events.add(event);
  }
}
