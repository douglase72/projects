package com.erdouglass.emdb.ingest.domain.model;

import java.time.Duration;
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
    var job = new IngestJob(IngestId.newId(), tmdbId, type, DateTimeFactory.now(), IngestStage.SUBMITTED);
    var msg = "Ingest for TMDB %s %s submitted.".formatted(type, tmdbId.value());
    job.raise(IngestSubmitted.of(job.id, job.submittedAt, msg));
    return job;
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
    var now = DateTimeFactory.now();
    var et = Duration.between(submittedAt.toInstant(), now.toInstant()).toMillis();
    var msg = "Ingest for TMDB %s %s started after sitting in the 'ingest-media' queue for %d ms."
        .formatted(type, tmdbId.value(), et);
    raise(IngestStarted.of(id, now, msg));
  }
  
  public void markExtracted() {
    transition(IngestStage.STARTED, IngestStage.EXTRACTED);
    var start = domainEvents.getLast().occurredAt().toInstant();
    var now = DateTimeFactory.now();
    var et = Duration.between(start, now.toInstant()).toMillis();
    var msg = "Ingest for TMDB %s %s extracted in %d ms.".formatted(type, tmdbId.value(), et);
    raise(IngestExtracted.of(id, now, msg));
  }
  
  public void fail(String cause) {
    var msg = "Ingest for TMDB %s %s failed.".formatted(type, tmdbId.value());
    raise(IngestFailed.of(id, DateTimeFactory.now(), msg));
  }
  
  public IngestId id() { return id; }
  public TmdbId tmdbId() { return tmdbId; }
  public IngestType type() { return type; }
  public DateTime submittedAt() { return submittedAt; }
  public IngestStage stage() { return stage; }
  public List<IngestEvent> events() { return domainEvents; }
  
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
