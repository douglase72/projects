package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.IngestStatus;
import com.erdouglass.emdb.ingest.domain.model.IngestType;

@Entity 
@Table(name = "ingest")
class IngestEntity {

  /// Internal surrogate key, application-generated ([IngestId] on the domain
  /// side).
  @Id
  private UUID id;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "ingest_type", nullable = false, updatable = false, length = 16)
  private IngestType ingestType;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private IngestStatus status;
  
  @Column(name = "submitted_at", nullable = false, updatable = false)
  private Instant submittedAt;
  
  @Column(name = "tmdb_id", nullable = false, updatable = false)
  private Integer tmdbId;
  
  IngestEntity() {}
  
  public void setId(UUID id) { this.id = id; }
  public UUID getId() { return id; }
  
  public void setType(IngestType ingestType) { this.ingestType = ingestType; }
  public IngestType getType() { return ingestType; }
  
  public void setStatus(IngestStatus status) { this.status = status; }
  public IngestStatus getStatus() { return status; }
  
  public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
  public Instant getSubmittedAt() { return submittedAt; }
  
  public void setTmdbId(Integer tmdbId) { this.tmdbId = tmdbId; }
  public Integer getTmdbId() { return tmdbId; }
}
