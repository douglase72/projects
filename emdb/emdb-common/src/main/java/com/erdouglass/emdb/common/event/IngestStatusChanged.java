package com.erdouglass.emdb.common.event;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.ShowConstants;
import com.fasterxml.jackson.annotation.JsonValue;

public record IngestStatusChanged(
    @NotNull UUID id,
    @NotNull IngestStatus status,
    @NotNull Instant lastModified,
    @NotNull @Positive Integer tmdbId,
    @NotNull IngestSource source,
    @NotNull MediaType type,
    @Positive Long emdbId,
    @Size(max = ShowConstants.TITLE_MAX_LENGTH) String name,
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
  
  public enum IngestSource {
    GATEWAY("emdb-gateway"),
    MEDIA("emdb-media"),
    SCHEDULER("emdb-scheduler"),
    SCRAPER("emdb-scraper"),
    USER("emdb-user");
    
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
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static Builder builder(IngestStatusChanged event) {
    return builder()        
        .id(event.id)
        .status(event.status)
        .lastModified(event.lastModified)
        .tmdbId(event.tmdbId)
        .source(event.source)
        .type(event.type)
        .emdbId(event.emdbId)
        .name(event.name)
        .message(event.message);  
  }
  
  @Override
  public String toString() {
    return "IngestStatusChanged[status=" + status()
        + ", tmdbId=" + tmdbId()
        + "]";
  }
  
  public static final class Builder {
    private Long emdbId;
    private UUID id;
    private String message;
    private String name;
    private IngestStatus status;
    private IngestSource source;
    private Instant lastModified = Instant.now();
    private Integer tmdbId;
    private MediaType type;
    
    Builder() {}
    
    public IngestStatusChanged build() {
      return new IngestStatusChanged(id, status, lastModified, tmdbId, source, type, emdbId, name, message);
    }
    
    public Builder emdbId(Long emdbId) {
      this.emdbId = emdbId;
      return this;
    }
    
    public Builder id(UUID id) {
      this.id = id;
      return this;
    }
    
    public Builder lastModified(Instant lastModified) {
      this.lastModified = lastModified;
      return this;
    }
    
    public Builder message(String message) {
      this.message = message;
      return this;
    }
    
    public Builder name(String name) {
      this.name = name;
      return this;
    }
    
    public Builder status(IngestStatus status) {
      this.status = status;
      return this;
    }
    
    public Builder source(IngestSource source) {
      this.source = source;
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
