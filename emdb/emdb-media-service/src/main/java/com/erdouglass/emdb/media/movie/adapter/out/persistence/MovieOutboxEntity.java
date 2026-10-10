package com.erdouglass.emdb.media.movie.adapter.out.persistence;

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
@Table(name = "movie_outbox")
public class MovieOutboxEntity {
  
  @Id
  private UUID id;
  
  @Column(name = "correlation_id", nullable = false, updatable = false)
  private UUID correlationId;
  
  @Column(name = "media_id", nullable = false, updatable = false)
  private UUID mediaId;
  
  @Column(name = "tmdb_id", nullable = false, updatable = false)
  private Integer tmdbId;

  MovieOutboxEntity() { }
}
