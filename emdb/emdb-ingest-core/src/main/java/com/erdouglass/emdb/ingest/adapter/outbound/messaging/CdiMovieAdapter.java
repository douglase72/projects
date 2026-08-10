package com.erdouglass.emdb.ingest.adapter.outbound.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.outbound.Movie;
import com.erdouglass.emdb.ingest.application.port.outbound.MovieEmitter;
import com.erdouglass.emdb.media.MovieExtractedEvent;

@ApplicationScoped
class CdiMovieAdapter implements MovieEmitter {
  
  @Inject
  Event<MovieExtractedEvent> emitter;

  @Override
  public void emit(Movie movie) {
    var event = new MovieExtractedEvent(
        movie.tmdbId(), movie.title(), movie.releaseDate(), movie.score(), movie.originalLanguage());
    emitter.fire(event);
  }
}
