package com.erdouglass.emdb.ingest.domain.event;

import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

public record IngestStarted(
    IngestId id, 
    TmdbId tmdbId, 
    IngestType type, 
    DateTime submittedAt) implements IngestEvent {

  public IngestStarted {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(type, "type is required");
    Objects.requireNonNull(submittedAt, "submittedAt is required");
  }
  
  public static IngestStarted of(IngestId id, TmdbId tmdbId, IngestType type, DateTime submittedAt) {
    return new IngestStarted(id, tmdbId, type, submittedAt);
  }
}
