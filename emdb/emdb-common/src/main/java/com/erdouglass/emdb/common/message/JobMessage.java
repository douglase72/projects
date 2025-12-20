package com.erdouglass.emdb.common.message;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonValue;

public record JobMessage(
    @NotNull UUID id,
    @NotNull Instant timestamp,
    @NotNull JobSource source,
    @NotNull JobStatus status,
    @NotBlank String content,
    @NotNull @Min(0) @Max(100) Integer progress) {
  
  public enum JobSource {
    GATEWAY("emdb-gateway-service"),
    MEDIA("emdb-media-service"),
    SCHEDULER("emdb-scheduler_service"),
    SCRAPER("emdb-scraper-service"),
    USER("emdb-user-service");
    
    private final String source;
    
    JobSource(String source) {
      this.source = source;
    }
    
    @Override
    @JsonValue
    public String toString() {
      return source;
    }
  }
  
  public enum JobStatus {
    SUBMITTED("Submitted"),
    STARTED("Started"),
    PROGRESS("Progress"),
    COMPLETED("Completed"),
    FAILED("Failed");
    
    private final String status;
    
    JobStatus(String status) {
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
    private UUID id;
    private Instant timestamp = Instant.now();
    private JobSource source;
    private JobStatus status;
    private String content;
    private Integer progress;
    
    Builder() { }
    
    public JobMessage build() {
      return new JobMessage(id, timestamp, source, status, content, progress);
    }
    
    public Builder content(String content) {
      this.content = content;
      return this;
    }
    
    public Builder id(UUID id) {
      this.id = id;
      return this;
    }
    
    public Builder progress(Integer progress) {
      this.progress = progress;
      return this;
    }
    
    public Builder source(JobSource source) {
      this.source = source;
      return this;
    } 
    
    public Builder status(JobStatus status) {
      this.status = status;
      return this;
    }
    
    public Builder timestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }  
  }

}
