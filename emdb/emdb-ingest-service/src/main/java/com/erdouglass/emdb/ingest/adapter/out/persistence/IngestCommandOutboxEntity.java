package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.erdouglass.emdb.shared.kernel.MediaType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "ingest_command_outbox")
public class IngestCommandOutboxEntity {
  
  @Id
  private UUID id;
  
  @Column(name = "ingest_id", nullable = false, updatable = false)
  private UUID ingestId;
  
  @Column(name = "submitted_at", nullable = false, updatable = false)
  private Instant submittedAt;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "media_type", nullable = false, updatable = false, length = 16)
  private MediaType mediaType;
  
  @Column(name = "tmdb_id", nullable = false, updatable = false)
  private Integer tmdbId;
  
  IngestCommandOutboxEntity() { }
}
