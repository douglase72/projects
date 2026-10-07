package com.erdouglass.emdb.media.messaging;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.PublicId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Builder;

@Builder
public record MediaEvent(
    MessageId messageId,
    PublicId correlationId,
    PublicId mediaId,
    TmdbId tmdbId,    
    Instant occurredAt,
    MediaType mediaType,
    EventType eventType) {

  public MediaEvent {
    Objects.requireNonNull(messageId, "messageId is required");
    Objects.requireNonNull(correlationId, "correlationId is required");
    Objects.requireNonNull(mediaId, "mediaId is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    occurredAt = DateTimeFactory.now().toInstant();
    Objects.requireNonNull(mediaType, "mediaType is required");
    Objects.requireNonNull(eventType, "eventType is required");
  }
  
  public enum EventType {
    SAVED,
    UPDATED,
    DELETED;
  }
}
