package com.erdouglass.emdb.media.movie.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.scraper.messaging.MovieExtracted;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class MovieConsumer {
  
  @Inject
  SaveMovieUseCase saveUseCase;

  @Blocking
  @Incoming("extract-movies")
  void onMessage(MovieExtracted event) {
    var command = SaveMovieCommand.of(event.correlationId(), event.tmdbId());
    saveUseCase.save(command);
  }
}
