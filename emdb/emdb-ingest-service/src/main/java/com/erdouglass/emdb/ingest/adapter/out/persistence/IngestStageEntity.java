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

import com.erdouglass.emdb.ingest.domain.model.IngestStage;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "ingest_stage")
class IngestStageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name = "job_id", nullable = false, updatable = false)
  private UUID jobId;
  
  @Column(nullable = false, length = 1000)
  private String message;
  
  @Column(name = "occurred_at", nullable = false, updatable = false)
  private Instant occurredAt;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private IngestStage stage;
  
  IngestStageEntity() { }
}
