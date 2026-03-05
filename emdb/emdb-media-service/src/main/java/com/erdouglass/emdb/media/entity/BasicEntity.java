package com.erdouglass.emdb.media.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.uuid.Generators;

/// Base class for all JPA entities in the application, providing common auditing and identity 
/// management.
///
/// This class implements a highly robust identity strategy. It uses a database-generated 
/// sequential ID for optimal index performance, combined with an immutable, time-based 
/// UUIDv7 (`uid`) generated at instantiation. This ensures entities can be safely placed 
/// in `HashSet`s prior to persistence without degrading database insert performance via index 
/// fragmentatition.
@MappedSuperclass
public abstract class BasicEntity<T> {
  public static final String SEQUENCE_GENERATOR = "sequence_generator";

  /// The timestamp when this entity was first persisted. This is automatically
  /// managed by Hibernate.
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant created;

  /// The surrogate primary key for the entity.
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_GENERATOR)
  private T id;

  /// The timestamp when this entity was last updated. This is automatically
  /// managed by Hibernate.
  @UpdateTimestamp
  @Column(nullable = false)
  private Instant modified;
  
  /// A time-ordered, database-friendly UUID used strictly for safe Java object equality.
  @Column(nullable = false, updatable = false, unique = true)
  private final UUID uid = Generators.timeBasedEpochGenerator().generate();  

  /// Default protected constructor for JPA and proxying.
  protected BasicEntity() {

  }

  public Instant getCreated() {
    return created;
  }

  public T getId() {
    return id;
  }

  public Instant getModified() {
    return modified;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BasicEntity<?> other)) return false;
    return uid != null && uid.equals(other.uid);
  }

  @Override
  public int hashCode() {
    return uid.hashCode();
  }

}