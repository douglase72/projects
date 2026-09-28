package com.erdouglass.emdb.ingest.messaging;

import java.util.Objects;

import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record IngestMediaMessage(TmdbId tmdbId, MediaType mediaType) {

  public IngestMediaMessage {
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(mediaType, "mediaType is required");
  }
  
  public static IngestMediaMessage of(TmdbId tmdbId, MediaType mediaType) {
    return new IngestMediaMessage(tmdbId, mediaType);
  }
}
