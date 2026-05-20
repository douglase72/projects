package com.erdouglass.emdb.ingest;

import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.LoggingDecorator;
import com.erdouglass.emdb.common.Configuration;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Publishes [IngestMedia] commands to RabbitMQ over the `ingest-media-out`
/// channel.
///
/// Each outgoing message is tagged with:
///
/// - a **correlation ID** — a time-based (v7) UUID generated per send,
///   returned to the caller and propagated as the AMQP correlation ID so
///   the request can be traced end-to-end through logs and metrics;
/// - a **start time** header ([Configuration#START_TIME]) capturing the
///   instant of publication, used downstream to measure queue dwell time;
/// - an **event type** header ([LoggingDecorator#EVENT_TYPE]) carrying the
///   simple class name of the payload to support routing and structured
///   logging on the consumer side.
///
/// The producer is application-scoped so the UUID generator and emitter
/// are reused across all callers.
@ApplicationScoped
public class IngestProducer {
  private final NoArgGenerator generator = Generators.timeBasedEpochGenerator();
  
  @Inject
  @Channel("ingest-media-out") 
  Emitter<IngestMedia> emitter;
  
  /// Publishes an ingest command to the broker.
  ///
  /// A fresh correlation ID is generated for every call — callers should
  /// not reuse one across submissions. The send is asynchronous: this
  /// method returns once the message has been handed to the emitter, not
  /// once the broker has acknowledged it.
  ///
  /// @param command the validated command to publish
  /// @return the correlation ID assigned to this submission  
  public UUID send(@NotNull @Valid final IngestMedia command) {
    var correlationId = generator.generate();
    emitter.send(Message.of(command)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withCorrelationId(correlationId.toString())
            .withHeader(Configuration.START_TIME, Instant.now().toString())
            .withHeader(LoggingDecorator.EVENT_TYPE, command.getClass().getSimpleName())
            .build()));
    return correlationId;
  }
}
