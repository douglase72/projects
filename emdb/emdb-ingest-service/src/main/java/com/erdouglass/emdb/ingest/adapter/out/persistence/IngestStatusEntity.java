package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.time.Duration;
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

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "ingest_status")
class IngestStatusEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name = "ingest_id", nullable = false, updatable = false)
  private UUID ingestId;
  
  @Column(name = "occurred_at", nullable = false, updatable = false)
  private Instant occurredAt;
  
  @Column(name = "queue_duration", updatable = false)
  private Duration queueDuration;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private IngestStatus status;
  
  IngestStatusEntity() { }
}
