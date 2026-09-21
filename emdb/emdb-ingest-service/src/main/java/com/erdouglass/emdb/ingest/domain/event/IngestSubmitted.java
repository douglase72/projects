package com.erdouglass.emdb.ingest.domain.event;

import java.util.Objects;

import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

public record IngestSubmitted(
    IngestId id, 
    TmdbId tmdbId, 
    IngestType type) implements IngestEvent {

  public IngestSubmitted {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(type, "type is required");
  }
  
  public static IngestSubmitted of(IngestId id, TmdbId tmdbId, IngestType type) {
    return new IngestSubmitted(id, tmdbId, type);
  }
}
