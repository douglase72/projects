package com.erdouglass.emdb.media.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.internal.ConsumerUtilities;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);

  @Inject
  MovieService service;
  
  @Inject
  ConsumerUtilities utils;

  @RunOnVirtualThread
  @Incoming("save-movie-in")
  public Uni<Void> onMessage(Message<SaveMovie> message) { 
    var command = message.getPayload();
    
    try {
      utils.validate(command);
      service.save(command);
      LOGGER.infof("Ingest of TMDB movie %d completed in %d ms", command.tmdbId(), utils.elapsedTime(message));
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      LOGGER.errorf(e, "Failed to save TMDB movie: ", command.tmdbId());
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
}
