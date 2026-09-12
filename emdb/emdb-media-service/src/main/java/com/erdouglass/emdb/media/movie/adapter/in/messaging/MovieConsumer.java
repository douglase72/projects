package com.erdouglass.emdb.media.movie.adapter.in.messaging;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  SaveMovieUseCase saveUseCase;
  
  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public CompletionStage<Void> onMessage(Message<SaveMovieCommand> message) {    
    try {
      var command = message.getPayload();
      saveUseCase.save(command);
      return message.ack();
    } catch (Exception e) {
      LOGGER.errorf(e , "Failed to save movie");
      return message.nack(e);
    }
  }
}
