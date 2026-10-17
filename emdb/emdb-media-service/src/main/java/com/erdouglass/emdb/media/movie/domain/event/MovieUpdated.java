package com.erdouglass.emdb.media.movie.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.media.shared.domain.model.MediaId;
import com.erdouglass.emdb.media.shared.domain.model.Title;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Builder;

@Builder
public record MovieUpdated(    
    MessageId id,
    CorrelationId correlationId,
    Instant occurredAt,
    MediaId mediaId,
    TmdbId tmdbId,
    Title title) implements MovieEvent {
  
  public MovieUpdated {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(correlationId, "correlationId must not be null");
    occurredAt = DateTimeFactory.now().toInstant();
    Objects.requireNonNull(mediaId, "mediaId must not be null");
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
  }
}
