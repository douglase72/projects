package com.erdouglass.emdb.common.message;

import java.time.Instant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuditMessage(
    @NotBlank String jobId,
    @NotNull Instant timestamp,
    @NotNull MessageSource source,
    @NotNull MessageType type,
    @NotBlank String message,
    @NotNull @Min(0) @Max(100) Integer progress) {
  
  public static AuditMessage of(
      String jobId, MessageSource source, MessageType type, String message, Integer progress) {
    return new AuditMessage(jobId, Instant.now(), source, type, message, progress);
  }
  
  public enum MessageSource {
    GATEWAY("emdb-gateway-service"),
    MEDIA("emdb-media-service"),
    SCHEDULER("emdb-scheduler_service"),
    SCRAPER("emdb-scraper-service"),
    USER("emdb-user-service");
    
    private final String source;
    
    MessageSource(String source) {
      this.source = source;
    }
    
    @Override
    public String toString() {
      return source;
    }
  }
  
  public enum MessageType {
    SUBMITTED,
    STARTED,
    PROGRESS,
    COMPLETED,
    FAILED;
  }

}
