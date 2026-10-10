package com.erdouglass.emdb.media.movie.domain.event;

import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.media.shared.domain.model.MediaId;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Builder;

@Builder
public record MovieSaved(
    MessageId id,
    CorrelationId correlationId,
    MediaId mediaId,
    TmdbId tmdbId) implements MovieEvent {

  public MovieSaved {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(correlationId, "correlationId must not be null");
    Objects.requireNonNull(mediaId, "mediaId must not be null");
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
  }
}
