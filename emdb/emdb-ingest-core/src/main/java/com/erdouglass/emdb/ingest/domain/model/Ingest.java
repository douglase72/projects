package com.erdouglass.emdb.ingest.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.erdouglass.emdb.ingest.domain.event.IngestCompletedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestExtractedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestFailedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestLoadedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestStartedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmittedEvent;
import com.erdouglass.emdb.media.TmdbId;

public final class Ingest {

  private final IngestId id;
  private final TmdbId tmdbId;
  private final IngestType type;
  private final Instant submittedAt;
  private final List<IngestEvent> events = new ArrayList<>();
  
  private IngestStatus status;
  
  private Ingest(
      IngestId id, 
      TmdbId tmdbId,
      IngestType type,
      Instant submittedAt,
      IngestStatus status,
      String message) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.tmdbId = Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    this.type = Objects.requireNonNull(type, "type must not be null"); 
    this.submittedAt = Objects.requireNonNull(submittedAt, "submittedAt must not be null"); 
    this.status = Objects.requireNonNull(status, "status must not be null"); 
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static Ingest submit(IngestId id, TmdbId tmdbId, IngestType type) {
    var msg = "Ingest for TMDB %s: %s submitted.".formatted(type, tmdbId.value());
    var ingest = new Ingest(id, tmdbId, type, Instant.now(), IngestStatus.SUBMITTED, msg);
    ingest.record(IngestSubmittedEvent.of(id, tmdbId, msg));
    return ingest;
  }
  
  public void started() {
    transition(IngestStatus.SUBMITTED, IngestStatus.STARTED);
    var et = Duration.between(submittedAt, Instant.now()).toMillis();
    var msg = "Ingest for TMDB %s: %s started after sitting in the 'ingest-media' queue for %d ms."
        .formatted(type, tmdbId.value(), et);
    record(IngestStartedEvent.of(id, tmdbId, msg));
  }
  
  public void extracted() {
    transition(IngestStatus.STARTED, IngestStatus.EXTRACTED);
    var event = events.getLast();
    var et = Duration.between(event.occurredAt(), Instant.now()).toMillis();
    var msg = "Ingest for TMDB %s: %s extracted in %d ms.".formatted(type, tmdbId.value(), et);
    record(IngestExtractedEvent.of(id, tmdbId, msg));
  }
  
  public void loaded() {
    transition(IngestStatus.EXTRACTED, IngestStatus.LOADED);
    var event = events.getLast();
    var et = Duration.between(event.occurredAt(), Instant.now()).toMillis();
    var msg = "Ingest for TMDB %s: %s loaded in %d ms.".formatted(type, tmdbId.value(), et);
    record(IngestLoadedEvent.of(id, tmdbId, msg));
  }
  
  public void completed() {
    transition(IngestStatus.LOADED, IngestStatus.COMPLETED);
    var et = Duration.between(submittedAt, Instant.now()).toMillis();
    var msg = "Ingest for TMDB %s: %s completed in %d ms.".formatted(type, tmdbId.value(), et);
    record(IngestCompletedEvent.of(id, tmdbId, msg));
  }  
  
  public void failed() {
    status = IngestStatus.FAILED;
    var msg = "Ingest for TMDB %s: %s failed.".formatted(type, tmdbId.value());
    record(IngestFailedEvent.of(id, tmdbId, msg));
  }
  
  public IngestId id() { return id; }
  public TmdbId tmdbId() { return tmdbId; }
  public IngestType type() { return type; }
  public Instant submittedAt() { return submittedAt; }
  public IngestStatus status() { return status; }
  public IngestEvent lastEvent() { return events.getLast(); }
  
  private void record(IngestEvent event) {
    events.add(event);
  }
  
  private void transition(IngestStatus expected, IngestStatus next) {
    if (status != expected) {
      throw new IllegalStateException("Invalid state: " + status);
    }
    this.status = next;
  }
  
  public static final class Builder {
    private IngestId id;
    private TmdbId tmdbId;
    private IngestType type;
    private Instant submittedAt;
    private String message;
    private IngestStatus status;  
    
    private Builder() {}
    
    public Ingest build() {
      return new Ingest(id, tmdbId, type, submittedAt, status, message);
    }
    
    public Builder id(IngestId id) {
      this.id = id;
      return this;
    }
    
    public Builder message(String message) {
      this.message = message;
      return this;
    }   
    
    public Builder status(IngestStatus status) {
      this.status = status;
      return this;
    }    
    
    public Builder submittedAt(Instant submittedAt) {
      this.submittedAt = submittedAt;
      return this;
    }
    
    public Builder tmdbId(TmdbId tmdbId) {
      this.tmdbId = tmdbId;
      return this;
    } 
    
    public Builder type(IngestType type) {
      this.type = type;
      return this;
    } 
  }
}
