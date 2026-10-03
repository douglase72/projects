package com.erdouglass.emdb.ingest.messaging;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record IngestMediaMessage(
    UUID ingestId,
    Instant submittedAt,
    TmdbId tmdbId, 
    MediaType mediaType) {

  public IngestMediaMessage {
    Objects.requireNonNull(ingestId, "ingestId is required");
    Objects.requireNonNull(submittedAt, "submittedAt is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(mediaType, "mediaType is required");
  }
  
  public static IngestMediaMessage of(UUID ingestId, TmdbId tmdbId, MediaType mediaType) {
    return new IngestMediaMessage(ingestId, DateTimeFactory.now().toInstant(), tmdbId, mediaType);
  }
}
