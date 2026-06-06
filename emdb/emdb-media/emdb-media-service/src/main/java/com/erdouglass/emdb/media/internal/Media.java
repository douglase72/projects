package com.erdouglass.emdb.media.internal;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/// Root mapped superclass for every media entity. Provides the
/// Hibernate-managed audit timestamps, the immutable TMDB natural key used for
/// equality, and the shared sequence-generator name. The type parameter `T` is
/// the type of the TMDB identifier — [Integer] for numeric ids (shows, people)
/// and [String] for TMDB credit ids.
///
/// Equality is based solely on the natural key: two instances are equal when
/// their [#getTmdbId()] values match, independent of persistence state.
@MappedSuperclass
public abstract class Media<T> {
  
  /// Logical name of the shared JPA sequence generator. The identifier is
  /// declared on the subclass; each concrete entity binds this name to a real
  /// database sequence with its own `@SequenceGenerator`.
  public static final String SEQUENCE_GENERATOR = "sequence_generator";
  
  /// The timestamp when this entity was first persisted. This is automatically
  /// managed by Hibernate.
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;
  
  /// The timestamp when this entity was last updated. This is automatically
  /// managed by Hibernate.
  @UpdateTimestamp
  @Column(nullable = false)
  private Instant modifiedAt;
  
  /// The TMDB id provides a stable immutable natural key.
  @NotNull
  @Column(name = "tmdb_id", unique = true, updatable = false)
  private T tmdbId;
  
  protected Media() {}
  
  protected Media(final T tmdbId) {
    this.tmdbId = tmdbId;
  }
  
  public Instant getCreatedAt() {
    return createdAt;
  }
  
  public Instant getModifiedAt() {
    return modifiedAt;
  }
  
  public T getTmdbId() {
    return tmdbId;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || Hibernate.getClass(this) != Hibernate.getClass(obj))
      return false;
    Media<?> other = (Media<?>) obj;
    return Objects.equals(getTmdbId(), other.getTmdbId());
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(getTmdbId());
  }
}
