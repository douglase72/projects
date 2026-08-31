package com.erdouglass.emdb.ingest.api;

import java.util.Objects;

public record IngestCommand(
    Integer tmdbId,
    IngestType ingestType) {

  public IngestCommand {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(ingestType, "ingest type is required");
  }
  
  public static IngestCommand of(Integer tmdbId, IngestType ingestType) {
    return new IngestCommand(tmdbId, ingestType);
  }
}
