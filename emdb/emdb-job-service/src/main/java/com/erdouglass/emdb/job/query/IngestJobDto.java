package com.erdouglass.emdb.job.query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;

public record IngestJobDto(
    @NotNull UUID id,
    @NotNull Instant timestamp,
    @NotNull IngestStatus status,
    @Positive Long emdbId,
    @NotNull @Positive Integer tmdbId,
    @NotNull IngestSource source,
    @NotNull MediaType type,
    @Size(max = ShowConstants.TITLE_MAX_LENGTH) String name,
    @NotEmpty List<JobStatus> history) {
  
  public record JobStatus(
      @NotNull IngestStatus status,
      @NotNull Instant timestamp,
      @NotNull IngestSource source) { }  
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private Long emdbId;
    private List<JobStatus> history = new ArrayList<>();
    private UUID id;
    private String name;
    private IngestSource source;
    private IngestStatus status;
    private Instant timestamp = Instant.now();
    private Integer tmdbId;
    private MediaType type;
    
    Builder() { }
    
    public IngestJobDto build() {
      return new IngestJobDto(id, timestamp, status, emdbId, tmdbId, source, type, name, history);
    }
    
    public Builder emdbId(Long emdbId) {
      this.emdbId = emdbId;
      return this;
    }
    
    public Builder history(List<JobStatus> history) {
      this.history = List.copyOf(history);
      return this;
    }
    
    public Builder id(UUID id) {
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
