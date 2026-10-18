package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.erdouglass.emdb.media.messaging.MediaEvent.EventType;

@Entity
@Table(name = "movie_outbox")
public class MovieOutboxEntity {
  
  @Id
  private UUID id;
  
  @Column(name = "correlation_id", nullable = false, updatable = false)
  private UUID correlationId;
  
  @Column(name = "media_id", updatable = false)
  private UUID mediaId;
  
  @Column(updatable = false)
  private String reason;
  
  @Column(name = "tmdb_id", nullable = false, updatable = false)
  private Integer tmdbId;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false, length = 16)
  private EventType type;

  MovieOutboxEntity() { }
  
  public void setId(UUID id) { this.id = id; }
  public UUID getId() { return id; }
  public void setCorrelationId(UUID correlationId) { this.correlationId = correlationId; }
  public UUID getCorrelationId() { return correlationId; }
  public void setMediaId(UUID mediaId) { this.mediaId = mediaId; }
  public Optional<UUID> getMediaId() { return Optional.ofNullable(mediaId); }
  public void setReason(String reason) { this.reason = reason; }
  public Optional<String> getReason() { return Optional.ofNullable(reason); }
  public void setTmdbId(Integer tmdbId) { this.tmdbId = tmdbId; }
  public Integer getTmdbId() { return tmdbId; }
  public void setType(EventType type) { this.type = type; }
  public EventType getType() { return type; }
}
