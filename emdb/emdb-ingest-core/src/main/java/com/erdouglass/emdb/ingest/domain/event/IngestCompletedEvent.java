package com.erdouglass.emdb.ingest.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.media.TmdbId;

public record IngestCompletedEvent(
    IngestId id,
    TmdbId tmdbId,
    Instant occurredAt,
    String message) implements IngestEvent {

  public static IngestCompletedEvent of(IngestId id, TmdbId tmdbId, String message) {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(message, "message must not be null");
    return new IngestCompletedEvent(id, tmdbId, Instant.now(), message);
  }
}
