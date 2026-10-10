package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Builder;

@Builder
public record SaveMovieCommand(
    MessageId id,
    CorrelationId correlationId,
    TmdbId tmdbId,
    MovieDetails details) {

  public SaveMovieCommand {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(correlationId, "correlationId must not be null");
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(details, "details must not be null");
  }
}
