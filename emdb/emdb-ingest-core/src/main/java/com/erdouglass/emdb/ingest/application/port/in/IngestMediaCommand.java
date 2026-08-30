package com.erdouglass.emdb.ingest.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.ingest.domain.model.IngestType;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

public record IngestMediaCommand(TmdbId tmdbId, IngestType type) {

  public IngestMediaCommand {
    Objects.requireNonNull(tmdbId, "TMDB id must not be null");
    Objects.requireNonNull(type, "type must not be null");    
  }
  
  public static IngestMediaCommand of(TmdbId tmdbId, IngestType type) {
    return new IngestMediaCommand(tmdbId, type);
  }
}
