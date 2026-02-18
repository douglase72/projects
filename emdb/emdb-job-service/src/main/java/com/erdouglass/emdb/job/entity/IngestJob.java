package com.erdouglass.emdb.job.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.job.query.IngestJobDto.JobStatus;

@Entity
@Table(name = "Ingest_Jobs")
public class IngestJob {
  
  @Positive
  @Column(name = "emdb_id")
  private Long emdbId;
  
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private List<JobStatus> history = new ArrayList<>();
  
  @Id
  private UUID id;
  
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "current_source")
  private IngestSource currentSource;
  
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "current_status")
  private IngestStatus currentStatus;
  
  private String message;
  
  @NotNull
  private Instant modified;
  
  @Size(max = ShowConstants.TITLE_MAX_LENGTH)
  private String name;
  
  @NotNull
  @Positive
  @Column(name = "tmdb_id")
  private Integer tmdbId;
  
  @NotNull
  @Enumerated(EnumType.STRING)
  private MediaType type;
  
  public IngestJob() {
    
  }
  
  public void emdbId(Long emdbId) {
    this.emdbId = emdbId;
  }
  
  public Optional<Long> emdbId() {
    return Optional.ofNullable(emdbId);
  }
  
  public List<JobStatus> history() {
    return history;
  }
  
  public void id(UUID id) {
    this.id = id;
  }
  
  public UUID id() {
    return id;
  }
  
  public void message(String message) {
    this.message = message;
  }
  
  public String message() {
    return message;
  }
  
  public Instant modified() {
    return modified;
  }
  
  public void name(String name) {
    this.name = name;
  }

  public Optional<String> name() {
    return Optional.ofNullable(name);
  }
  
  public void status(IngestStatus status, Instant timestamp, IngestSource source, String message) {
    this.currentStatus = status;
    this.modified = timestamp;
    this.currentSource = source;
    this.history.add(new JobStatus(status, timestamp, source, message));
  }
  
  public IngestSource source() {
    return currentSource;
  }
  
  public IngestStatus status() {
    return currentStatus;
  }
  
  public void tmdbId(Integer tmdbId) {
    this.tmdbId = tmdbId;
  }
  
  public Integer tmdbId() {
    return tmdbId;
  }
  
  public void type(MediaType type) {
    this.type = type;
  }
  
  public MediaType type() {
    return type;
  }
  
  @Override
  public String toString() {
    return "IngestJob[id=" + id
        + ", modified=" + modified
        + ", status=" + currentStatus
        + ", emdbId=" + emdbId
        + ", tmdbId=" + tmdbId
        + ", source=" + currentSource
        + ", type=" + type
        + "]";
  }

}
