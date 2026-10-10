package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.erdouglass.emdb.ingest.domain.model.IngestStatus;
import com.erdouglass.emdb.shared.kernel.MediaType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "ingest_event")
public class IngestEventEntity {

  @Id
  private UUID id;
  
  @Column(name = "ingest_id", nullable = false, updatable = false)
  private UUID ingestId;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "media_type", nullable = false, updatable = false, length = 16)
  private MediaType mediaType;
  
  @Column(name = "occurred_at", nullable = false, updatable = false)
  private Instant occurredAt;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "ingest_status", nullable = false, length = 16)
  private IngestStatus status;
  
  @Column(name = "tmdb_id", nullable = false, updatable = false)
  private Integer tmdbId;
  
  IngestEventEntity() { }
}
