package com.erdouglass.emdb.ingest.domain.model;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.emdb.media.MediaType;
import com.erdouglass.emdb.media.SourceId;

public final class Ingest {

  private final IngestId id;
  private final MediaType mediaType;
  private final SourceId sourceId;
  private final Instant submittedAt;
  
  private String message;
  private IngestStatus status;
  
  private Ingest(IngestId id, SourceId sourceId, MediaType mediaType, IngestStatus status, String message) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.sourceId = Objects.requireNonNull(sourceId, "source id must not be null"); 
    this.mediaType = Objects.requireNonNull(mediaType, "media type must not be null");
    this.submittedAt = Instant.now();
    this.status = status;
    this.message = message;
  }
  
  public static Ingest submit(IngestId id, SourceId sourceId, MediaType mediaType) {
    var msg = "Ingest for %s %s: %s submitted.".formatted(sourceId.source(), mediaType, sourceId.id());
    var ingest = new Ingest(id, sourceId, mediaType, IngestStatus.SUBMITTED, msg);
    return ingest;
  }
  
  public IngestId id() { return id; }
  public MediaType mediaType() { return mediaType; }
  public String message() { return message; }
  public SourceId sourceId() { return sourceId; }
  public IngestStatus status() { return status; }
  public Instant submittedAt() { return submittedAt; }
}
