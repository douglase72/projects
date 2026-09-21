package com.erdouglass.emdb.ingest.domain.event;

import java.util.Objects;

import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

public record IngestExtracted(
    IngestId id, 
    TmdbId tmdbId, 
    IngestType type) implements IngestEvent {

  public IngestExtracted {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(type, "type is required");
  }
  
  public static IngestExtracted of(IngestId id, TmdbId tmdbId, IngestType type) {
    return new IngestExtracted(id, tmdbId, type);
  }
}
