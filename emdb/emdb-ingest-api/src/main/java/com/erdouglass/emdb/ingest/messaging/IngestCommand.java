package com.erdouglass.emdb.ingest.messaging;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Builder;

@Builder
public record IngestCommand(
    MessageId id,
    CorrelationId correlationId,
    Instant submittedAt,
    TmdbId tmdbId,
    MediaType mediaType) {

  public IngestCommand {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(correlationId, "correlationId is required");
    Objects.requireNonNull(submittedAt, "submittedAt is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(mediaType, "mediaType is required");
  }
}
