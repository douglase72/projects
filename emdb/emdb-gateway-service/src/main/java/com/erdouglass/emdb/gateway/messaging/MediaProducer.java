package com.erdouglass.emdb.gateway.messaging;

import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.event.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatus;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusProducer;
import com.erdouglass.messaging.LoggingDecorator;
import com.fasterxml.uuid.Generators;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Publishes [IngestMedia] commands to the RabbitMQ ingest-media exchange.
///
/// Generates a time-based UUID correlation ID for each job that is
/// attached as message metadata and returned to the caller for tracking.
/// The correlation ID is also placed in the MDC for log tracing.
@ApplicationScoped
public class MediaProducer {
  
  @Inject
  @Channel("ingest-media-out") 
  Emitter<IngestMedia> emitter;
  
  @Inject
  IngestStatusProducer producer;

  /// Publishes an ingestion command to the message broker.
  ///
  /// Attaches a correlation ID, the job start timestamp, and an event
  /// type header to the outgoing RabbitMQ message metadata.
  ///
  /// @param command the ingestion request to publish
  /// @return the generated correlation ID that serves as the job ID
  public UUID ingest(IngestMedia command) {
    var correlationId = Generators.timeBasedEpochGenerator().generate();
    MDC.put(LoggingDecorator.CORRELATION_ID, correlationId.toString());
    
    try {
      emitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
              .withRoutingKey(Configuration.INGEST_KEY)
              .withCorrelationId(correlationId.toString())
              .withHeader(Configuration.START_TIME, Instant.now().toString())
              .withHeader("X-Event-Type", command.getClass().getSimpleName())
              .build()));
      var msg = String.format("Ingest Job for TMDB %s %d submitted", command.type(), command.tmdbId());
      producer.send(IngestStatusChanged.builder()
          .id(correlationId)
          .tmdbId(command.tmdbId())
          .status(IngestStatus.SUBMITTED)
          .source(IngestSource.GATEWAY)
          .type(command.type())
          .message(msg)
          .build());
      return correlationId;
    } finally {
      MDC.remove(LoggingDecorator.CORRELATION_ID);
    }    
  }
}
