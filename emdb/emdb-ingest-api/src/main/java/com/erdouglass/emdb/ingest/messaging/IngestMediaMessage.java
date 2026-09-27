package com.erdouglass.emdb.ingest.messaging;

import java.util.Objects;

public record IngestMediaMessage(Integer tmdbId, String mediaType) {

  public IngestMediaMessage {
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(mediaType, "mediaType is required");
  }
  
  public static IngestMediaMessage of(Integer tmdbId, String mediaType) {
    return new IngestMediaMessage(tmdbId, mediaType);
  }
}
