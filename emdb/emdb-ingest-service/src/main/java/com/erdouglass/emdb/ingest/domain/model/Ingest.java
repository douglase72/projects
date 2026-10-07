package com.erdouglass.emdb.ingest.domain.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTime;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.domain.event.DomainEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestCompleted;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.exception.IllegalTransitionException;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.PublicId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class Ingest {
  private final PublicId id;
  private final TmdbId tmdbId;
  private final MediaType mediaType;
  private final DateTime submittedAt;
  
  private IngestStatus status;
  private final List<DomainEvent> events = new ArrayList<>();
  
  private Ingest(PublicId id, TmdbId tmdbId, MediaType mediaType, DateTime submittedAt, IngestStatus status) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.tmdbId = Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    this.mediaType = Objects.requireNonNull(mediaType, "mediaType must not be null");
    this.submittedAt = Objects.requireNonNull(submittedAt, "submittedAt must not be null");
    this.status = Objects.requireNonNull(status, "status must not be null");    
  }
  
  public static Ingest submit(TmdbId tmdbId, MediaType mediaType) {
    var job = new Ingest(PublicId.newId(), tmdbId, mediaType, DateTimeFactory.now(), IngestStatus.SUBMITTED);
    job.raise(IngestSubmitted.of(MessageId.newId(), job.id, tmdbId, mediaType));
    return job;
  }
  
  public void start() {
    transition(IngestStatus.SUBMITTED, IngestStatus.STARTED);
    var et = Duration.between(submittedAt.toInstant(), DateTimeFactory.now().toInstant());
    raise(IngestStarted.of(MessageId.newId(), id, tmdbId, mediaType, et));
  }
  
  public void extract() {
    transition(IngestStatus.STARTED, IngestStatus.EXTRACTED);
    raise(IngestExtracted.of(MessageId.newId(), id, tmdbId, mediaType));
  }
  
  public void complete() {
    transition(IngestStatus.EXTRACTED, IngestStatus.COMPLETED);
    raise(IngestCompleted.of(MessageId.newId(), id, tmdbId, mediaType));
  }
  
  public static Ingest rehydrate(
      PublicId id, 
      TmdbId tmdbId, 
      MediaType mediaType,
      DateTime submittedAt,
      IngestStatus status) {
    var job = new Ingest(id, tmdbId, mediaType, submittedAt, status);
    return job;
  }
  
  public List<DomainEvent> pullEvents() {
    var eventsToReturn = List.copyOf(events);
    events.clear();
    return eventsToReturn;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Ingest other = (Ingest) obj;
    return Objects.equals(id, other.id);
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
  
  private void raise(DomainEvent event) {
    this.events.add(event);
  }
  
  private void transition(IngestStatus expected, IngestStatus next) {
    if (status != expected) {
      throw new IllegalTransitionException(id, status, next);
    }
    status = next;
  }  
}
