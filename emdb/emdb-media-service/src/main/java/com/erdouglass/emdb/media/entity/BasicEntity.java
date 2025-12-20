package com.erdouglass.emdb.media.entity;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/// A base abstract class for all entities requiring a primary key,
/// business key (`tmdbId`), and auditing timestamps.
///
/// Equality is determined by the natural business key
/// ({@code tmdbId}), not the surrogate primary key ({@code id}).
///
/// @param <T> The data type of the TMDB identifier (e.g., {@code Integer}).
@MappedSuperclass
public abstract class BasicEntity<T> {
  public static final String SEQUENCE_GENERATOR = "sequence_generator";

  /// The timestamp when this entity was first persisted.
  /// This is automatically managed by Hibernate.
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant created;

  /// The surrogate primary key for the entity.
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_GENERATOR)
  private Long id;

  /// The timestamp when this entity was last updated.
  /// This is automatically managed by Hibernate.
  @UpdateTimestamp
  @Column(nullable = false)
  private Instant modified;
  
  /// The natural business key derived from The Movie Database (TMDB).
  /// This is immutable and determines the Java identity of the entity.
  @NotNull
  @Column(name = "tmdb_id", unique = true, updatable = false)
  private T tmdbId;

  /// Default protected constructor for JPA and proxying.
  protected BasicEntity() {
    
  }
  
  /// Creates a new entity with its required external business key.
  ///
  /// @param tmdbId The external identifier from TMDB.
  protected BasicEntity(T tmdbId) {
    this.tmdbId = tmdbId;
  }

  public Instant created() {
    return created;
  }

  public Long id() {
    return id;
  }

  public Instant modified() {
    return modified;
  }
  
  public T tmdbId() {
    return tmdbId;
  }

  /// Compares this entity to another object for equality.
  ///
  /// This implementation uses the natural business key
  /// ({@code tmdbId}) rather than the surrogate {@code id}.
  /// This allows for comparison of transient (unsaved) entities.
  ///
  /// It is proxy-safe, using {@code instanceof} instead of
  /// {@code getClass() != o.getClass()}.
  ///
  /// @param o The object to compare with.
  /// @return {@code true} if the objects are equal based on
  ///         the business key, {@code false} otherwise.
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !(o instanceof BasicEntity)) {
      return false;
    }
    BasicEntity<?> other = (BasicEntity<?>) o;
    return Objects.equals(tmdbId, other.tmdbId);
  }
  
  /// Generates a hash code for the entity based on its business key.
  ///
  /// @return The hash code of the {@code tmdbId}.
  @Override
  public int hashCode() {
    return Objects.hash(tmdbId);
  }
  
}
