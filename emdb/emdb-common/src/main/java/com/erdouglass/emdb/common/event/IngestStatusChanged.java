package com.erdouglass.emdb.common.event;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonValue;

public record IngestStatusChanged(
    @NotNull UUID id,
    @NotNull IngestStatus status,
    @NotNull Instant timestamp,
    @NotNull @Positive Integer tmdbId,
    String message) {

  public enum IngestStatus {
    SUBMITTED("Submitted"),
    STARTED("Started"),
    EXTRACTED("Extracted"),
    COMPLETED("Completed"),
    FAILED("Failed");
    
    private final String status;
    
    IngestStatus(String status) {
      this.status = status;
    }
    
    @Override
    @JsonValue
    public String toString() {
      return status;
    }    
  }
  
  public static Builder builder() {
    return new Builder();
  }  
  
  @Override
  public String toString() {
    return "IngestStatusChanged[status=" + status()
        + ", tmdbId=" + tmdbId()
        + "]";
  }
  
  public static final class Builder {
    private UUID id;
    private String message;
    private IngestStatus status;
    private Instant timestamp = Instant.now();
    private Integer tmdbId;
    
    Builder() {}
    
    public IngestStatusChanged build() {
      return new IngestStatusChanged(id, status, timestamp, tmdbId, message);
    }
    
    public Builder id(UUID id) {
      this.id = id;
      return this;
    }
    
    public Builder message(String message) {
      this.message = message;
      return this;
    }
    
    public Builder status(IngestStatus status) {
      this.status = status;
      return this;
    }
    
    public Builder timestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }
    
    public Builder tmdbId(Integer tmdbId) {
      this.tmdbId = tmdbId;
      return this;
    } 
  }
}
