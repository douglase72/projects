package com.erdouglass.emdb.media.movie.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.messaging.MovieScraped;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  SaveMovieUseCase saveUseCase;

  @Blocking
  @Incoming("movies-scraped")
  void onMessage(MovieScraped event) {
    LOGGER.infof("Received: %s", event);
    var command = MovieMapper.toMovieScrapedEvent(event);
    saveUseCase.save(command);
  }
}
