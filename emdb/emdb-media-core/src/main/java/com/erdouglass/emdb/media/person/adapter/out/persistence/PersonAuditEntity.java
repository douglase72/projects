package com.erdouglass.emdb.media.person.adapter.out.persistence;

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

import com.erdouglass.emdb.media.kernel.FieldOperation;
import com.erdouglass.emdb.media.person.domain.PersonField;

/// A row of the append-only audit table.
///
/// Every mapped column is `updatable = false`: once written, a row is immutable,
/// and the entity offers no way to change one through JPA. The class carries no
/// foreign key to the movie table either, so history outlives the people it
/// describes.
///
/// Values are stored as unbounded text because they hold the rendered form of
/// heterogeneous fields — a two-character language code and a four-thousand
/// character synopsis share the columns.
///
/// The sequence allocates in blocks of fifty, since a single write commonly
/// inserts several rows and a round trip per row would dominate the cost.
///
/// Two indexes serve the two ways history gets read: one persons timeline, and
/// everything that happened in a window.
@Entity
@Table(name = "person_audit", indexes = {
  @Index(name = "ix_person_audit_person",    columnList = "person_surrogate_id, occurred_at, id"),
  @Index(name = "ix_person_audit_occurred", columnList = "occurred_at")
})
class PersonAuditEntity {
  
  @Enumerated(EnumType.STRING)
  @Column(name = "field_name", nullable = false, updatable = false, length = 64)
  private PersonField field;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_audit_seq")
  @SequenceGenerator(name = "person_audit_seq", sequenceName = "person_audit_seq", allocationSize = 50)
  private Long id;
  
  @Column(name = "person_public_id", nullable = false, updatable = false, length = 32)
  private String personPublicId; 
  
  @Column(name = "person_surrogate_id", nullable = false, updatable = false)
  private UUID personSurrogateId;
  
  @Column(name = "new_value", updatable = false, columnDefinition = "text")
  private String newValue;
  
  @Column(name = "occurred_at", nullable = false, updatable = false)
  private Instant occurredAt;
  
  @Column(name = "old_value", updatable = false, columnDefinition = "text")
  private String oldValue;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false, length = 16)
  private FieldOperation operation;
  
  PersonAuditEntity() {}
  
  public void setField(PersonField field) { this.field = field; }
  public PersonField getField() { return field; }
  
  public Long getId() { return id; }
  
  public void setPersonPublicId(String moviePublicId) { this.personPublicId = moviePublicId; }
  public String getPersonPublicId() { return personPublicId; }
  
  public void setPersonSurrogateId(UUID movieSurrogateId) { this.personSurrogateId = movieSurrogateId; }
  public UUID getPersonSurrogateId() { return personSurrogateId; }
  
  public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
  public Instant getOccurredAt() { return occurredAt; }
  
  public void setOldValue(String oldValue) { this.oldValue = oldValue; }
  public String getOldValue() { return oldValue; }
  
  public void setNewValue(String newValue) { this.newValue = newValue; }
  public String getNewValue() { return newValue; }
  
  public void setOperation(FieldOperation operation) { this.operation = operation; }
  public FieldOperation getOperation()  { return operation; }
}