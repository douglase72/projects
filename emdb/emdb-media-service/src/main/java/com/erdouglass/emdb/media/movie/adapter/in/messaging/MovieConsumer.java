package com.erdouglass.emdb.media.movie.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.messaging.SaveMovieMessage;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  SaveMovieUseCase saveUseCase;

  @RunOnVirtualThread
  @Incoming("save-movie")
  void onMessage(SaveMovieMessage message) {
    LOGGER.infof("Received: %s", message);
    var command = MovieMapper.toSaveMovieCommand(message);
    saveUseCase.save(command);
  }
}
