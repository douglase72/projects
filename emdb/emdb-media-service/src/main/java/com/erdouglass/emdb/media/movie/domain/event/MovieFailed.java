package com.erdouglass.emdb.media.movie.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.media.shared.domain.model.Title;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Builder;

@Builder
public record MovieFailed(
    MessageId id,
    CorrelationId correlationId,
    Instant occurredAt,
    TmdbId tmdbId,
    Title title,
    String reason) implements MovieEvent {

  public MovieFailed {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(correlationId, "correlationId must not be null");
    occurredAt = occurredAt != null ? occurredAt : DateTimeFactory.now().toInstant();
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(reason, "reason must not be null");
  }
}
