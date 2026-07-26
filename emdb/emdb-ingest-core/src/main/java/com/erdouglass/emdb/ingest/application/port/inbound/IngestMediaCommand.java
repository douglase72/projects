package com.erdouglass.emdb.ingest.application.port.inbound;

import java.util.Objects;

import com.erdouglass.emdb.ingest.domain.model.TmdbId;
import com.erdouglass.emdb.media.MediaType;

public record IngestMediaCommand(TmdbId tmdbId, MediaType mediaType) {

  public IngestMediaCommand {
    Objects.requireNonNull(tmdbId, "tmdb id must not be null");
    Objects.requireNonNull(mediaType, "media type must not be null");
  }
  
  public static IngestMediaCommand of(TmdbId tmdbId, MediaType mediaType) {
    return new IngestMediaCommand(tmdbId, mediaType);
  }
}
