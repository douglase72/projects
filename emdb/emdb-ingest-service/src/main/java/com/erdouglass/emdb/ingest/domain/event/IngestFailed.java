package com.erdouglass.emdb.ingest.domain.event;

import java.util.Objects;

import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

public record IngestFailed(
    IngestId id, 
    TmdbId tmdbId, 
    IngestType type, 
    String cause) implements IngestEvent {

  public IngestFailed {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(type, "type is required");
    Objects.requireNonNull(cause, "cause is required");
  }
  
  public static IngestFailed of(IngestId id, TmdbId tmdbId, IngestType type, String cause) {
    return new IngestFailed(id, tmdbId, type, cause);
  }  
}
