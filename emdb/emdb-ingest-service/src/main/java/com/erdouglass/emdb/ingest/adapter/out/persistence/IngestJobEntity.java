package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;
import com.erdouglass.emdb.ingest.domain.model.IngestStage;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity 
@Table(name = "ingest_job")
class IngestJobEntity {

  @Id
  private UUID id;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "ingest_type", nullable = false, updatable = false, length = 16)
  private IngestType ingestType;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private IngestStage stage;
  
  @Column(name = "submitted_at", nullable = false, updatable = false)
  private Instant submittedAt;
  
  @Column(name = "tmdb_id", nullable = false, updatable = false)
  private Integer tmdbId;
  
  @Version
  @Column(name = "version", nullable = false)
  Long version;
  
  IngestJobEntity() { }
}
