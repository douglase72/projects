package com.erdouglass.emdb.ingest.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.PublicId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record IngestCompleted(
    MessageId messageId,
    PublicId ingestId, 
    Instant occurredAt, 
    TmdbId tmdbId,
    MediaType mediaType) implements DomainEvent {

  public IngestCompleted {
    Objects.requireNonNull(messageId, "messageId is required");
    Objects.requireNonNull(ingestId, "ingestId is required");
    Objects.requireNonNull(occurredAt, "occurredAt is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(mediaType, "mediaType is required");
  }
  
  public static IngestCompleted of(
      MessageId messageId, PublicId ingestId, TmdbId tmdbId, MediaType mediaType) {
    return new IngestCompleted(messageId, ingestId, DateTimeFactory.now().toInstant(), tmdbId, mediaType);
  }  
}
