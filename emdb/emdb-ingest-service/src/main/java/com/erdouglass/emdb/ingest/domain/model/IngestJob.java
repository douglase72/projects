package com.erdouglass.emdb.ingest.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestFailed;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.exception.IllegalTransitionException;

import lombok.experimental.Accessors;

@Accessors(fluent = true)
public final class IngestJob {
  private final IngestId id;
  private final TmdbId tmdbId;
  private final IngestType type;
  private final DateTime submittedAt;
  
  private IngestStage stage;
  private final List<IngestEvent> domainEvents = new ArrayList<>();
  
  private IngestJob(IngestId id, TmdbId tmdbId, IngestType type, DateTime submittedAt, IngestStage stage) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.tmdbId = Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    this.type = Objects.requireNonNull(type, "type must not be null");
    this.submittedAt = Objects.requireNonNull(submittedAt, "submittedAt must not be null");
    this.stage = Objects.requireNonNull(stage, "stage must not be null");
  }
  
  public static IngestJob submit(TmdbId tmdbId, IngestType type) {
    var ingest = new IngestJob(IngestId.newId(), tmdbId, type, DateTimeFactory.now(), IngestStage.SUBMITTED);
    ingest.raise(IngestSubmitted.of(ingest.id, tmdbId, type));
    return ingest;
  }
  
  public static IngestJob rehydrate(
      IngestId id, 
      TmdbId tmdbId, 
      IngestType type,
      DateTime submittedAt,
      IngestStage stage) {
    return new IngestJob(id, tmdbId, type, submittedAt, stage);
  }
  
  public void start() {
    transition(IngestStage.SUBMITTED, IngestStage.STARTED);
    raise(IngestStarted.of(id, tmdbId, type, submittedAt));
  }
  
  public void markExtracted() {
    transition(IngestStage.STARTED, IngestStage.EXTRACTED);
    raise(IngestExtracted.of(id, tmdbId, type));
  }
  
  public void fail(String cause) {
    stage = IngestStage.FAILED;
    raise(IngestFailed.of(id, tmdbId, type, cause));
  }
  
  public List<IngestEvent> pullEvents() {
    var events = List.copyOf(domainEvents);
    domainEvents.clear();
    return events;
  }
  
  public IngestId id() { return id; }
  public TmdbId tmdbId() { return tmdbId; }
  public IngestType type() { return type; }
  public DateTime submittedAt() { return submittedAt; }
  public IngestStage stage() { return stage; }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id" + id.value()
      + ", tmdbId=" + tmdbId.value()
      + ", type=" + type
      + ", submittedAt=" + submittedAt
      + ", stage=" + stage
      + "]";
  }
  
  private void raise(IngestEvent event) {
    this.domainEvents.add(event);
  }
  
  private void transition(IngestStage expected, IngestStage next) {
    if (stage != expected) {
      throw new IllegalTransitionException(id, expected, next);
    }
    stage = next;
  }
}
