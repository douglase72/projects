package com.erdouglass.emdb.media.movie;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.common.validation.CommandValidator;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.movie.SaveMovie;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

/// Consumes [SaveMovie] commands from the RabbitMQ save-movie queue.
@ApplicationScoped
class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieService service;
  
  @Inject
  CommandValidator validator;

  /// Validates and persists an incoming [SaveMovie] command, then
  /// acknowledges or negatively-acknowledges the underlying RabbitMQ message
  /// based on the outcome.
  ///
  /// On a successful save, logs the end-to-end ingest latency computed from
  /// the `START_TIME` header set upstream. On a [ConstraintViolationException]
  /// the message is nacked at WARN level — the broker is expected to route it
  /// to the dead-letter queue handled by [MovieDeadLetterConsumer]. Any other
  /// exception is nacked at ERROR level.
  ///
  /// Runs on a virtual thread so the blocking JPA and HTTP work inside
  /// [MovieService#save] does not occupy a Vert.x event-loop thread.
  ///
  /// @param message the incoming RabbitMQ message carrying a [SaveMovie] payload
  /// @return a [Uni] that completes when the ack or nack has been dispatched  
  @RunOnVirtualThread
  @Incoming("save-movie-in")
  public Uni<Void> onMessage(Message<SaveMovie> message) {
    var command = message.getPayload();
    
    try {
      validator.validate(command);
      service.save(mapper.toMovie(command));
      
      var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
          .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
      var start = Instant.parse(metadata.getHeaders().get(Configuration.START_TIME).toString());
      var et = Duration.between(start, Instant.now()).toMillis();
      LOGGER.infof("Ingest of TMDB movie %d completed in %d ms", command.tmdbId(), et);
      return Uni.createFrom().completionStage(message.ack());
    } catch (ConstraintViolationException e) {
      LOGGER.warnf(e, "Validation failed for TMDB movie %d", command.tmdbId());
      return Uni.createFrom().completionStage(message.nack(e));
    } catch (Exception e) {
      LOGGER.errorf(e, "Failed to save TMDB movie %d", command.tmdbId());
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
}
