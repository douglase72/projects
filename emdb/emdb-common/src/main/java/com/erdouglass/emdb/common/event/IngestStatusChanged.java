package com.erdouglass.emdb.common.event;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.ShowConstants;
import com.fasterxml.jackson.annotation.JsonValue;

public record IngestStatusChanged(
    @NotNull IngestStatus status,
    @NotNull String id,
    @NotNull Instant timestamp,
    @Positive Long emdbId,
    @NotNull @Positive Integer tmdbId,
    @NotNull IngestSource source,
    @NotNull MediaType type,
    @Size(max = ShowConstants.TITLE_MAX_LENGTH) String name) {
  
  public enum IngestSource {
    GATEWAY("emdb-gateway-service"),
    MEDIA("emdb-media-service"),
    SCHEDULER("emdb-scheduler-service"),
    USER("emdb-user-service");
    
    private final String source;
    
    IngestSource(String source) {
      this.source = source;
    }
    
    @Override
    @JsonValue
    public String toString() {
      return source;
    }
  }
  
  public enum IngestStatus {
    SUBMITTED("Submitted"),
    STARTED("Started"),
    EXTRACTED("Extracted"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    HEARTBEAT("Heartbeat");
    
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
  
  public static final class Builder {
    private Long emdbId;
    private String id;
    private String name;
    private IngestSource source;
    private IngestStatus status;
    private Instant timestamp = Instant.now();
    private Integer tmdbId;
    private MediaType type;
    
    Builder() { }
    
    public IngestStatusChanged build() {
      return new IngestStatusChanged(status, id, timestamp, emdbId, tmdbId, source, type, name);
    }
    
    public Builder emdbId(Long emdbId) {
      this.emdbId = emdbId;
      return this;
    }
    
    public Builder id(String id) {
      this.id = id;
      return this;
    }
    
    public Builder name(String name) {
      this.name = name;
      return this;
    }
    
    public Builder source(IngestSource source) {
      this.source = source;
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
    
    public Builder type(MediaType type) {
      this.type = type;
      return this;
    }      
  }

}
