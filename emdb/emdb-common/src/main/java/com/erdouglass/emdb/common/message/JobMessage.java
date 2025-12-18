package com.erdouglass.emdb.common.message;

import java.time.Instant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonValue;

public record JobMessage(
    @NotBlank String id,
    @NotNull Instant timestamp,
    @NotNull JobSource source,
    @NotNull JobStatus status,
    @NotBlank String content,
    @NotNull @Min(0) @Max(100) Integer progress) {
  
  public static JobMessage of(
      String jobId, JobSource source, JobStatus status, String content, Integer progress) {
    return new JobMessage(jobId, Instant.now(), source, status, content, progress);
  }
  
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

}
