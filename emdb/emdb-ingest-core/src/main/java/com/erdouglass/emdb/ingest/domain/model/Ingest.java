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

public final class Ingest {
  private final IngestId id;
  private final IngestSource source;
  private final Instant submittedAt;
  private final Map<IngestStatus, IngestEvent> pendingEvents = new HashMap<>();
  
  private String message;
  private IngestStatus status;
  
  private Ingest(
      IngestId id, 
      IngestSource source, 
      Instant submittedAt, 
      IngestStatus status, 
      String message) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.source = Objects.requireNonNull(source, "source id must not be null"); 
    this.submittedAt = submittedAt;
    this.status = status;
    this.message = message;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static Ingest submit(IngestId id, IngestSource is) {
    var msg = "Ingest for %s %s: %s submitted.".formatted(is.provider(), is.type(), is.id());
    var ingest = new Ingest(id, is, Instant.now(), IngestStatus.SUBMITTED, msg);
    ingest.submitted();
    return ingest;
  }
  
  public void started() {
    status = IngestStatus.STARTED;
    var et = Duration.between(submittedAt, Instant.now()).toMillis();
    message = "Ingest for %s %s: %s sat in the 'ingest-media' queue for %d ms."
        .formatted(source.provider(), source.type(), source.id(), et);
    pendingEvents.put(IngestStatus.STARTED, IngestStartedEvent.of(id, message));
  }
  
  public void extracted() {
    status = IngestStatus.EXTRACTED;
    var startedEvent = pendingEvents.get(IngestStatus.STARTED);
    if (startedEvent == null) {
      throw new IllegalStateException("invalid ingest state");
    }
    var et = Duration.between(startedEvent.occurredAt(), Instant.now()).toMillis();
    message = "Ingest for %s %s: %s extracted in %d ms."
        .formatted(source.provider(), source.type(), source.id(), et);  
    pendingEvents.put(IngestStatus.EXTRACTED, IngestExtractedEvent.of(id, message));
  }
  
  public void loaded() {
    status = IngestStatus.LOADED;
    var extractedEvent = pendingEvents.get(IngestStatus.EXTRACTED);
    if (extractedEvent == null) {
      throw new IllegalStateException("invalid ingest state");
    }
    var et = Duration.between(extractedEvent.occurredAt(), Instant.now()).toMillis();
    message = "Ingest for %s %s: %s loaded in %d ms."
        .formatted(source.provider(), source.type(), source.id(), et); 
    pendingEvents.put(IngestStatus.LOADED, IngestLoadedEvent.of(id, message));
  }
  
  public void completed() {
    status = IngestStatus.COMPLETED;
    var et = Duration.between(submittedAt, Instant.now()).toMillis();
    message = "Ingest for %s %s: %s completed in %d ms."
        .formatted(source.provider(), source.type(), source.id(), et);
    pendingEvents.put(IngestStatus.COMPLETED, IngestCompletedEvent.of(id, message));
  }
  
  public void failed() {
    status = IngestStatus.FAILED;
    message = "Ingest for %s %s: %s failed."
        .formatted(source.provider(), source.type(), source.id());
    pendingEvents.put(IngestStatus.FAILED, IngestFailedEvent.of(id, message));
  }
  
  public IngestId id() { return id; }
  public String message() { return message; }
  public IngestSource source() { return source; }
  public IngestStatus status() { return status; }
  public Instant submittedAt() { return submittedAt; }
  
  public static final class Builder {
    private IngestId id;
    private IngestSource source;
    private Instant submittedAt;
    private String message;
    private IngestStatus status;  
    
    private Builder() {}
    
    public Ingest build() {
      return new Ingest(id, source, submittedAt, status, message);
    }
    
    public Builder id(IngestId id) {
      this.id = id;
      return this;
    }
    
    public Builder message(String message) {
      this.message = message;
      return this;
    }   
    
    public Builder source(IngestSource source) {
      this.source = source;
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
  }
  
  private void submitted() {
    pendingEvents.put(IngestStatus.SUBMITTED, IngestSubmittedEvent.of(id, message));
  }
}
