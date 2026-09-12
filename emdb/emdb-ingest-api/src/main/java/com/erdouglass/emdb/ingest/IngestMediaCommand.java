package com.erdouglass.emdb.ingest;

import java.util.Objects;

import com.erdouglass.emdb.common.TmdbId;

public record IngestMediaCommand(TmdbId tmdbId, IngestType ingestType) {

  public IngestMediaCommand {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(ingestType, "ingest type is required");
  }
  
  public static IngestMediaCommand of(TmdbId tmdbId, IngestType ingestType) {
    return new IngestMediaCommand(tmdbId, ingestType);
  }
}
