package com.erdouglass.emdb.ingest.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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
  private final Map<IngestStatus, IngestEvent> events = new HashMap<>();
  
  private String message;
  private IngestStatus status;
  
  private Ingest(
      IngestId id, 
      TmdbId tmdbId,
      IngestType type,
      Instant submittedAt, 
      IngestStatus status, 
      String message) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.tmdbId = Objects.requireNonNull(tmdbId, "TMDB id must not be null"); 
    this.type = Objects.requireNonNull(type, "type must not be null"); 
    this.submittedAt = Objects.requireNonNull(submittedAt, "submittedAt must not be null"); 
    this.status = Objects.requireNonNull(status, "status must not be null"); 
    this.message = Objects.requireNonNull(message, "message must not be null"); 
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static Ingest submit(IngestId id, TmdbId tmdbId, IngestType type) {
    var msg = "Ingest for TMDB %s: %s submitted.".formatted(type, tmdbId.value());
    var ingest = new Ingest(id, tmdbId, type, Instant.now(), IngestStatus.SUBMITTED, msg);
    ingest.submitted();
    return ingest;
  }
  
  public void started() {
    status = IngestStatus.STARTED;
    var et = Duration.between(submittedAt, Instant.now()).toMillis();
    message = "Ingest for TMDB %s: %s sat in the 'ingest-media' queue for %d ms."
        .formatted(type, tmdbId.value(), et);
    events.put(IngestStatus.STARTED, IngestStartedEvent.of(id, message));
  }
  
  public void extracted() {
    status = IngestStatus.EXTRACTED;
    var startedEvent = events.get(IngestStatus.STARTED);
    if (startedEvent == null) {
      throw new IllegalStateException("invalid ingest state");
    }
    var et = Duration.between(startedEvent.occurredAt(), Instant.now()).toMillis();
    message = "Ingest for TMDB %s: %s extracted in %d ms."
        .formatted(type, tmdbId.value(), et);
    events.put(IngestStatus.EXTRACTED, IngestExtractedEvent.of(id, message));
  }
  
  public void loaded() {
    status = IngestStatus.LOADED;
    var extractedEvent = events.get(IngestStatus.EXTRACTED);
    if (extractedEvent == null) {
      throw new IllegalStateException("invalid ingest state");
    }
    var et = Duration.between(extractedEvent.occurredAt(), Instant.now()).toMillis();
    message = "Ingest for TMDB %s: %s loaded in %d ms."
        .formatted(type, tmdbId.value(), et);
    events.put(IngestStatus.LOADED, IngestLoadedEvent.of(id, message));
  }
  
  public void completed() {
    status = IngestStatus.COMPLETED;
    var et = Duration.between(submittedAt, Instant.now()).toMillis();
    message = "Ingest for TMDB %s: %s completed in %d ms."
        .formatted(type, tmdbId.value(), et);
    events.put(IngestStatus.COMPLETED, IngestCompletedEvent.of(id, message));
  }
  
  public void failed() {
    status = IngestStatus.FAILED;
    message = "Ingest for TMDB %s: %s failed.".formatted(type, tmdbId.value());
    events.put(IngestStatus.FAILED, IngestFailedEvent.of(id, message));
  }
  
  public IngestId id() { return id; }
  public String message() { return message; }
  public TmdbId tmdbId() { return tmdbId; }
  public IngestType type() { return type; }
  public IngestStatus status() { return status; }
  public Instant submittedAt() { return submittedAt; }
  
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
  
  private void submitted() {
    events.put(IngestStatus.SUBMITTED, IngestSubmittedEvent.of(id, message));
  }
}
