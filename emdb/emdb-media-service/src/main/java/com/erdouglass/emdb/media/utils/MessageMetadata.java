package com.erdouglass.emdb.media.utils;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

public final class MessageMetadata {
  private MessageMetadata() {}
  
  public static UUID getCorrelationId(Message<?> message) {
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
        .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
    return metadata.getCorrelationId()
        .map(UUID::fromString)
        .orElseThrow(() -> new NoSuchElementException("No correlation id."));
  }
}
