package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.erdouglass.emdb.ingest.domain.model.IngestStatus;

@Entity
@Table(name = "ingest_event")
class IngestEventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name = "ingest_id", nullable = false, updatable = false)
  private UUID ingestId;
  
  @Column(length = 1000)
  private String message;
  
  @Column(name = "occurred_at", nullable = false, updatable = false)
  private Instant occurredAt;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private IngestStatus status;
  
  IngestEventEntity() {}
  
  public void setIngestId(UUID ingestId) { this.ingestId = ingestId; }
  public UUID getIngestId() { return ingestId; }
  
  public void setMessage(String message) { this.message = message; }
  public String getMessage() { return message; }
  
  public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
  public Instant getOccurredAt() { return occurredAt; }
  
  public void setStatus(IngestStatus status) { this.status = status; }
  public IngestStatus getStatus() { return status; }
}
