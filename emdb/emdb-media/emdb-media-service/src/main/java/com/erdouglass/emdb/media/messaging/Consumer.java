package com.erdouglass.emdb.media.messaging;

import java.time.Duration;
import java.time.Instant;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.media.SaveCommand;
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.person.SavePerson;
import com.erdouglass.emdb.media.series.SaveSeries;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

public abstract class Consumer<T extends SaveCommand> {
  private static final Logger LOGGER = Logger.getLogger(Consumer.class);
  
  @Inject 
  Validator validator;
  
  public abstract Uni<Void> onMessage(Message<T> message);
  
  /// Validates and persists an incoming command, then acknowledges or 
  /// negatively-acknowledges the underlying RabbitMQ message based on the 
  /// outcome.
  ///
  /// On a successful save, logs the end-to-end ingest latency computed from
  /// the `START_TIME` header set upstream. On a [ConstraintViolationException]
  /// the message is nacked and the broker is expected to route it to the 
  /// dead-letter queue. 
  /// 
  /// Runs on a virtual thread so the blocking persistence work inside
  /// [Consumer#save] does not occupy a Vert.x event-loop thread.
  ///
  /// @param message the incoming RabbitMQ message carrying the command
  /// @return a [Uni] that completes when the ack or nack has been dispatched   
  protected Uni<Void> consume(Message<T> message) {
    T command = message.getPayload();
    
    try {
      validate(command);
      save(command);
      var et = elapsedTime(message);
      LOGGER.infof("Ingest of %s completed in %d ms", append(command), et);
      return Uni.createFrom().completionStage(message.ack());
    } catch (ConstraintViolationException e) {
      LOGGER.errorf(e, "Validation failed for %s", append(command));
      return Uni.createFrom().completionStage(message.nack(e));
    } catch (Exception e) {
      LOGGER.errorf(e, "Failed to save %s", append(command));
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
  
  protected abstract void save(T command);
  
  private long elapsedTime(Message<T> message) {
    return message.getMetadata(IncomingRabbitMQMetadata.class)
        .map(m -> m.getHeaders().get(IngestMedia.START_TIME))
        .map(Object::toString)
        .map(Instant::parse)
        .map(start -> Duration.between(start, Instant.now()).toMillis())
        .orElse(0L);
  }
  
  private String append(T command) {
    return switch (command) {
      case SaveMovie m -> String.format("TMDB movie %d", m.tmdbId()); 
      case SaveSeries s -> String.format("TMDB series %d", s.tmdbId()); 
      case SavePerson p -> String.format("TMDB person %d", p.tmdbId()); 
    };    
  }
  
  private void validate(T command) {
    var violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
