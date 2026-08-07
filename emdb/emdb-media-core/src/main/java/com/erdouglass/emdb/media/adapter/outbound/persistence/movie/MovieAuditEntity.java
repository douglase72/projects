package com.erdouglass.emdb.media.adapter.outbound.persistence.movie;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.erdouglass.emdb.media.domain.movie.MovieField;
import com.erdouglass.emdb.media.domain.shared.FieldOperation;

@Entity
@Table(name = "movie_audit", indexes = {
        @Index(name = "ix_movie_audit_movie",    columnList = "movie_surrogate_id, occurred_at, id"),
        @Index(name = "ix_movie_audit_occurred", columnList = "occurred_at")
})
class MovieAuditEntity {
  
  @Enumerated(EnumType.STRING)
  @Column(name = "field_name", nullable = false, updatable = false, length = 64)
  private MovieField field;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_audit_seq")
  @SequenceGenerator(name = "movie_audit_seq", sequenceName = "movie_audit_seq", allocationSize = 50)
  private Long id;
  
  @Column(name = "movie_public_id", nullable = false, updatable = false, length = 32)
  private String moviePublicId; 
  
  @Column(name = "movie_surrogate_id", nullable = false, updatable = false)
  private UUID movieSurrogateId;
  
  @Column(name = "new_value", updatable = false, columnDefinition = "text")
  private String newValue;
  
  @Column(name = "occurred_at", nullable = false, updatable = false)
  private Instant occurredAt;
  
  @Column(name = "old_value", updatable = false, columnDefinition = "text")
  private String oldValue;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false, length = 16)
  private FieldOperation operation;
  
  MovieAuditEntity() {}
  
  public void setField(MovieField field) { this.field = field; }
  public MovieField getField() { return field; }
  
  public Long getId() { return id; }
  
  public void setMoviePublicId(String moviePublicId) { this.moviePublicId = moviePublicId; }
  public String getMoviePublicId() { return moviePublicId; }
  
  public void setMovieSurrogateId(UUID movieSurrogateId) { this.movieSurrogateId = movieSurrogateId; }
  public UUID getMovieSurrogateId() { return movieSurrogateId; }
  
  public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
  public Instant getOccurredAt() { return occurredAt; }
  
  public void setOldValue(String oldValue) { this.oldValue = oldValue; }
  public String getOldValue() { return oldValue; }
  
  public void setNewValue(String newValue) { this.newValue = newValue; }
  public String getNewValue() { return newValue; }
  
  public void setOperation(FieldOperation operation) { this.operation = operation; }
  public FieldOperation getOperation()  { return operation; }
}
