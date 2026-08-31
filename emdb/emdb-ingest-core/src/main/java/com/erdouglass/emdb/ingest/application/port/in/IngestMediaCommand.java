package com.erdouglass.emdb.ingest.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.ingest.api.IngestType;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

public record IngestMediaCommand(TmdbId tmdbId, IngestType mediaType) {

  public IngestMediaCommand {
    Objects.requireNonNull(tmdbId, "TMDB id must not be null");
    Objects.requireNonNull(mediaType, "type must not be null");    
  }
  
  public static IngestMediaCommand of(TmdbId tmdbId, IngestType mediaType) {
    return new IngestMediaCommand(tmdbId, mediaType);
  }
}
