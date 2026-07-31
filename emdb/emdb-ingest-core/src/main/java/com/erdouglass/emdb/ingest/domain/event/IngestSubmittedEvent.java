package com.erdouglass.emdb.ingest.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public record IngestSubmittedEvent(
    IngestId id,
    Instant occurredAt,
    String message) implements IngestEvent {

  public static IngestSubmittedEvent of(IngestId id, String message) {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(message, "message must not be null");
    return new IngestSubmittedEvent(id, Instant.now(), message);
  }
}
