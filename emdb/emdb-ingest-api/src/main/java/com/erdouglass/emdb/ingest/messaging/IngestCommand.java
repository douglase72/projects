package com.erdouglass.emdb.ingest.messaging;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.PublicId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Builder;

@Builder
public record IngestCommand(
    MessageId messageId,
    PublicId correlationId,
    TmdbId tmdbId, 
    Instant submittedAt,
    MediaType mediaType) {
  
  public IngestCommand {
    Objects.requireNonNull(messageId, "messageId is required");
    Objects.requireNonNull(correlationId, "correlationId is required");
    submittedAt = DateTimeFactory.now().toInstant();
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(mediaType, "mediaType is required");
  }
}
