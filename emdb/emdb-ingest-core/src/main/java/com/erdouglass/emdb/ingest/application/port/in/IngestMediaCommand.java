package com.erdouglass.emdb.ingest.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.ingest.domain.model.IngestType;
import com.erdouglass.emdb.media.api.TmdbId;

public record IngestMediaCommand(TmdbId tmdbId, IngestType type) {
  
  public IngestMediaCommand {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(type, "ingest type is required");
  }
  
  public static IngestMediaCommand of(TmdbId tmdbId, IngestType type) {
    return new IngestMediaCommand(tmdbId, type);
  }
}
