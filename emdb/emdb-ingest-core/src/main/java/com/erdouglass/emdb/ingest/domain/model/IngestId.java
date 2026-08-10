package com.erdouglass.emdb.ingest.domain.model;

import java.util.Objects;
import java.util.UUID;

public record IngestId(UUID value) {
  
  public IngestId {
    Objects.requireNonNull(value, "ingest id must not be null");
  }
  
  public static IngestId of(UUID id) {
    return new IngestId(id);
  }
  
  public static IngestId from(String id) {
    return new IngestId(UUID.fromString(id));
  }
}
