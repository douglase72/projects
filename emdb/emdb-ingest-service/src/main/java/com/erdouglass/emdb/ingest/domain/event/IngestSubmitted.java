package com.erdouglass.emdb.ingest.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.PublicId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record IngestSubmitted(
    PublicId ingestId, 
    Instant occurredAt, 
    TmdbId tmdbId,
    MediaType mediaType) implements IngestEvent {

  public IngestSubmitted {
    Objects.requireNonNull(ingestId, "ingestId is required");
    Objects.requireNonNull(occurredAt, "occurredAt is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(mediaType, "mediaType is required");
  }
  
  public static IngestSubmitted of(PublicId ingestId, TmdbId tmdbId, MediaType mediaType) {
    return new IngestSubmitted(ingestId, DateTimeFactory.now().toInstant(), tmdbId, mediaType);
  }  
}
