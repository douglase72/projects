package com.erdouglass.emdb.common.command;

import java.time.Instant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AuditMetadata(
    @NotBlank String traceId,
    @NotNull Instant timestamp,
    @NotNull EventSource source,
    @NotNull EventType type,
    @NotBlank String message,
    @NotNull @Min(0) @Max(100) Integer percentComplete,
    @Positive Long latency) {
  
  public enum EventSource {
    GATEWAY("emdb-gateway-service"),
    MEDIA("emdb-media-service"),
    SCHEDULER("emdb-scheduler_service"),
    SCRAPER("emdb-scraper-service"),
    USER("emdb-user-service");
    
    private final String source;
    
    EventSource(String source) {
      this.source = source;
    }
    
    @Override
    public String toString() {
      return source;
    }
  }
  
  public enum EventType {
    SUBMITTED,
    STARTED,
    PROGRESS,
    COMPLETED,
    FAILED;
  }

}
