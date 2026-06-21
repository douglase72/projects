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
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Publishes [IngestMedia] commands to the RabbitMQ `ingest-media-out` channel.
///
/// Each published message is tagged with a freshly generated correlation id and
/// an event-type header so downstream consumers and logging decorators can trace
/// the ingest end-to-end.
@ApplicationScoped
public class IngestProducer {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();

  @Inject
  @Channel("ingest-media-out")
  Emitter<IngestMedia> emitter;

  /// Publishes the given command and returns its generated correlation id.
  ///
  /// The correlation id is attached as RabbitMQ metadata and also returned to the
  /// caller so the originator of the request can correlate the asynchronous
  /// outcome.
  ///
  /// @param command the validated ingest command to publish; must be non-null
  /// @return the correlation id assigned to the outgoing message
  public UUID publish(@NotNull @Valid IngestMedia command) {
    var correlationId = GENERATOR.generate();
    emitter.send(Message.of(command)
        .addMetadata(OutgoingRabbitMQMetadata.builder().withCorrelationId(correlationId.toString())
            .withHeader(LoggingDecorator.EVENT_TYPE, command.getClass().getSimpleName())
            .withHeader(IngestMedia.START_TIME, Instant.now().toString()).build()));
    return correlationId;
  }
}
