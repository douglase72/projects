package com.erdouglass.emdb.shared.kernel;

import java.util.Objects;
import java.util.UUID;

public record CorrelationId(UUID value) {
  
  public CorrelationId {
    Objects.requireNonNull(value, "id is required");
  }
  
  public static CorrelationId of(UUID id) { return new CorrelationId(id); }
}
