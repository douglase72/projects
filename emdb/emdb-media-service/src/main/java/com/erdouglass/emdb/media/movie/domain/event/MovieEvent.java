package com.erdouglass.emdb.media.movie.domain.event;

import java.time.Instant;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.media.shared.domain.model.Title;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public sealed interface MovieEvent permits MovieCreated, MovieUpdated, MovieFailed {
  
  MessageId id();
  CorrelationId correlationId();
  Instant occurredAt();
  TmdbId tmdbId();
  Title title();
}
