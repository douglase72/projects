package com.erdouglass.emdb.media.person.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.erdouglass.emdb.media.person.domain.model.Name;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "person_outbox")
public class PersonOutboxEntity {

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;
  
  @Id
  private UUID id;
  
  @Column(nullable = false, updatable = false, length = Name.MAX_LENGTH)
  private String name;
  
  @Column(name = "tmdb_id", nullable = false, updatable = false)
  private Integer tmdbId;
  
  PersonOutboxEntity() { }
}
