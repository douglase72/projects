package com.erdouglass.emdb.ingest.adapter.outbound.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.IngestStatus;
import com.erdouglass.emdb.media.MediaType;

@Entity 
@Table(
  name = "ingest",
  uniqueConstraints = {
    @UniqueConstraint(name = "uq_ingest_source", columnNames = { "source", "source_id" }),
  }
)
class IngestEntity {

  /// Internal surrogate key, application-generated ([IngestId] on the domain
  /// side).
  @Id
  private UUID id;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "media_type", nullable = false, length = 16)
  private MediaType mediaType;
  
  @Column(length = 1000)
  private String message;
  
  @Column(nullable = false, length = 16)
  private String source;
  
  @Column(name = "source_id", nullable = false, length = 64)
  private String sourceId;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private IngestStatus status;
  
  @Column(name = "submitted_at", nullable = false)
  private Instant submittedAt;
  
  IngestEntity() {}
  
  public void setId(UUID id) {
    this.id = id;
  }
  
  public UUID getId() {
    return id;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMediaType(MediaType mediaType) {
    this.mediaType = mediaType;
  }
  
  public MediaType getMediaType() {
    return mediaType;
  }
  
  public void setSource(String source) {
    this.source = source;
  }
  
  public String getSource() {
    return source;
  }
  
  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }
  
  public String getSourceId() {
    return sourceId;
  }  
  
  public void setStatus(IngestStatus status) {
    this.status = status;
  }
  
  public IngestStatus getStatus() {
    return status;
  }
  
  public void setSubmittedAt(Instant submittedAt) {
    this.submittedAt = submittedAt;
  }
  
  public Instant getSubmittedAt() {
    return submittedAt;
  }
}
