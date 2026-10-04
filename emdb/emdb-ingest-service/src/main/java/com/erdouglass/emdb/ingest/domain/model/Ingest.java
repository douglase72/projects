package com.erdouglass.emdb.ingest.domain.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestLoaded;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.exception.IllegalTransitionException;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class Ingest {
  private final IngestId id;
  private final TmdbId tmdbId;
  private final MediaType mediaType;
  private final DateTime submittedAt;
  
  private IngestStatus status;
  private final List<IngestEvent> events = new ArrayList<>();
  
  private Ingest(IngestId id, TmdbId tmdbId, MediaType mediaType, DateTime submittedAt, IngestStatus status) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.tmdbId = Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    this.mediaType = Objects.requireNonNull(mediaType, "mediaType must not be null");
    this.submittedAt = Objects.requireNonNull(submittedAt, "submittedAt must not be null");
    this.status = Objects.requireNonNull(status, "status must not be null");
  }
  
  public static Ingest submit(TmdbId tmdbId, MediaType mediaType) {
    var job = new Ingest(IngestId.newId(), tmdbId, mediaType, DateTimeFactory.now(), IngestStatus.SUBMITTED);
    job.raise(IngestSubmitted.of(job.id, tmdbId, mediaType));
    return job;
  }
  
  public void start() {
    transition(IngestStatus.SUBMITTED, IngestStatus.STARTED);
    var et = Duration.between(submittedAt.toInstant(), DateTimeFactory.now().toInstant());
    raise(IngestStarted.of(id, tmdbId, mediaType, et));
  }
  
  public void extract() {
    transition(IngestStatus.STARTED, IngestStatus.EXTRACTED);
    raise(IngestExtracted.of(id, tmdbId, mediaType));
  }
  
  public void load() {
    transition(IngestStatus.EXTRACTED, IngestStatus.LOADED);
    raise(IngestLoaded.of(id, tmdbId, mediaType));
  }  
  
  public static Ingest rehydrate(
      IngestId id, 
      TmdbId tmdbId, 
      MediaType mediaType,
      DateTime submittedAt,
      IngestStatus status) {
    var job = new Ingest(id, tmdbId, mediaType, submittedAt, status);
    return job;
  }
  
  public List<IngestEvent> pullEvents() {
    var eventsToReturn = List.copyOf(events);
    events.clear();
    return eventsToReturn;
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id" + id.value()
      + ", tmdbId=" + tmdbId.value()
      + ", mediaType=" + mediaType
      + ", submittedAt=" + submittedAt
      + ", status=" + status
      + "]";
  }
  
  private void raise(IngestEvent event) {
    this.events.add(event);
  }
  
  private void transition(IngestStatus expected, IngestStatus next) {
    if (status != expected) {
      throw new IllegalTransitionException(id, status, next);
    }
    status = next;
  }
}
