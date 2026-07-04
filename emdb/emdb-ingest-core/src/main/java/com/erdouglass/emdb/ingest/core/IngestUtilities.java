package com.erdouglass.emdb.ingest.core;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.IngestMedia;

import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

public final class IngestUtilities {

  private IngestUtilities() {}
  
  public static UUID correlationId(Message<?> message) {
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
        .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
    return metadata.getCorrelationId()
        .map(UUID::fromString)
        .orElseThrow(() -> new IllegalArgumentException("Invalid correlation id"));    
  }
  
  public static Instant ingestStart(Message<?> message) {
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
        .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
    return Optional.ofNullable(metadata.getHeaders())
        .map(h -> Instant.parse(h.get(IngestMedia.START_TIME).toString()))
        .orElseThrow(() -> new IllegalArgumentException("Invalid ingest start time"));
  }
}
