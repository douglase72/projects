package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity 
@Table(name = "ingest_event")
class EventEntity {
  
  @Id
  private UUID id;
  
  @Column(name = "processed_at", nullable = false, updatable = false)
  private Instant processedAt;

  EventEntity() { }
}
