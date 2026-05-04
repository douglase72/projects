package com.erdouglass.emdb.common.api.messaging;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.api.MediaType;
import com.erdouglass.emdb.common.api.ShowConstants;

/// Domain event published every time an ingest job changes state.
///
/// Emitted by the gateway-service on submission and by the media-service
/// at each subsequent transition. Consumers include the notification-service
/// (which persists the event and its history) and the gateway-service's
/// SSE [IngestService] (which broadcasts it to UI clients).
///
/// Equality is based on every field; idempotent processing should rely on
/// the combination of [#id] and [#lastModified].
///
/// @param id           correlation ID identifying the ingest job; stable
///                     across all events for the same job
/// @param status       the status the job has just transitioned into
/// @param lastModified the instant at which the transition occurred
/// @param tmdbId       the TMDB ID being ingested
/// @param source       the service that produced this event
/// @param type         the type of media being ingested
/// @param emdbId       the assigned EMDB ID, populated once known
/// @param name         the title of the media, populated once known
/// @param message      a human-readable description of the transition
public record IngestStatusChanged(
    UUID id,
    @NotNull IngestStatus status,
    @NotNull Instant lastModified,
    @NotNull @Positive Integer tmdbId,
    @NotNull IngestSource source,
    @NotNull MediaType type,
    @Positive Long emdbId,
    @Size(max = ShowConstants.TITLE_MAX_LENGTH) String name,
    @NotNull String message) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static Builder builder(IngestStatusChanged event) {
    return builder()        
        .id(event.id)
        .status(event.status)
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
