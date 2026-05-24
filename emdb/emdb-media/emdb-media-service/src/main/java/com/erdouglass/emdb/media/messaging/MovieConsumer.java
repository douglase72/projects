package com.erdouglass.emdb.media.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.domain.MovieService;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

/// Consumes [SaveMovie] commands from the RabbitMQ save-movie queue.
@ApplicationScoped
public class MovieConsumer extends Consumer<SaveMovie> {
  
  @Inject
  MovieService service;

  /// Validates and persists an incoming [SaveMovie] command, then
  /// acknowledges or negatively-acknowledges the underlying RabbitMQ message
  /// based on the outcome.
  ///
  /// On a successful save, logs the end-to-end ingest latency computed from
  /// the `START_TIME` header set upstream. On a [ConstraintViolationException]
  /// the message is nacked and the broker is expected to route it to the 
  /// dead-letter queue handled by [MovieDeadLetterConsumer]. 
  /// 
  /// Runs on a virtual thread so the blocking persistence work inside
  /// [MovieService#save] does not occupy a Vert.x event-loop thread.
  ///
  /// @param message the incoming RabbitMQ message carrying a [SaveMovie] payload
  /// @return a [Uni] that completes when the ack or nack has been dispatched    
  @RunOnVirtualThread
  @Incoming("save-movie-in")
  public Uni<Void> onMessage(Message<SaveMovie> message) { 
    return consume(message);
  }

  @Override
  protected void save(SaveMovie command) {
    service.save(command);
  }
}
