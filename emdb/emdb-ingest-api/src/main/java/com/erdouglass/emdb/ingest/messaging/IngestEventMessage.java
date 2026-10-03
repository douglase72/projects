package com.erdouglass.emdb.ingest.messaging;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import lombok.Builder;

@Builder
public record IngestEventMessage(
    UUID id,
    UUID ingestId,
    Instant occurredAt,
    Integer tmdbId,
    String mediaType,
    EventType eventType) {
  
  public IngestEventMessage {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(ingestId, "ingestId is required");
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
