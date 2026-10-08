package com.erdouglass.emdb.ingest.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record IngestMediaCommand(TmdbId tmdbId, MediaType mediaType) {

  public IngestMediaCommand {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(mediaType, "media type is required");
  }
  
  public static IngestMediaCommand of(TmdbId tmdbId, MediaType mediaType) {
    return new IngestMediaCommand(tmdbId, mediaType);
  }
}
