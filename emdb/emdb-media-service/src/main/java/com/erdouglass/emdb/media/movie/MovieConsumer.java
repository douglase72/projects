package com.erdouglass.emdb.media.movie;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.command.SaveMovie;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

/// Consumes [SaveMovie] commands from the RabbitMQ save-movie queue.
@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieService service;

  @RunOnVirtualThread
  @Incoming("save-movie-in")
  public Uni<Void> onMessage(Message<SaveMovie> message) {
    try {
      var command = message.getPayload();
      service.save(mapper.toMovie(command));
      
      var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
          .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
      var start = Instant.parse(metadata.getHeaders().get(Configuration.START_TIME).toString());
      var et = Duration.between(start, Instant.now()).toMillis();
      LOGGER.infof("Ingest of TMDB movie %d completed in %d ms", command.tmdbId(), et);
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      LOGGER.error("Failed to save media", e);
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
}
