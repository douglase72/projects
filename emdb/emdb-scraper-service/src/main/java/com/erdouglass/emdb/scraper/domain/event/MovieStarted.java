package com.erdouglass.emdb.scraper.domain.event;

import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record MovieStarted(MessageId id, CorrelationId correlationId, TmdbId tmdbId) implements MovieEvent {

  public MovieStarted {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(correlationId, "correlationId must not be null");
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
  }
  
  public static MovieStarted of(MessageId id, CorrelationId correlationId, TmdbId tmdbId) {
    return new MovieStarted(id, correlationId, tmdbId);
  }
}
