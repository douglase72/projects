package com.erdouglass.emdb.ingest.messaging;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Builder;

@Builder
public record IngestEvent(
    MessageId messageId,
    CorrelationId correlationId,
    Instant occurredAt,
    TmdbId tmdbId,
    MediaType mediaType,
    EventType eventType) {

  public IngestEvent {
    Objects.requireNonNull(messageId, "messageId is required");
    Objects.requireNonNull(correlationId, "correlationId is required");
    Objects.requireNonNull(occurredAt, "occurredAt is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(mediaType, "mediaType is required");
    Objects.requireNonNull(eventType, "eventType is required");
  }

  public enum EventType {
    SUBMITTED,
    STARTED,
    EXTRACTED,
    COMPLETED,
    FAILED;
  }  
}
