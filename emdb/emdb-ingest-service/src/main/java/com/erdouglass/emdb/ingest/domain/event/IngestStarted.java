package com.erdouglass.emdb.ingest.domain.event;

import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

public record IngestStarted(
    IngestId id, 
    DateTime occurredAt, 
    String message) implements IngestEvent {

  public IngestStarted {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(occurredAt, "occurredAt is required");
    Objects.requireNonNull(message, "type is required");
  }
  
  public static IngestStarted of(IngestId id, DateTime occurredAt, String message) {
    return new IngestStarted(id, occurredAt, message);
  }
}
