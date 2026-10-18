package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record ReportMovieFailureCommand(
    MessageId id,
    CorrelationId correlationId,
    TmdbId tmdbId,
    String reason) {

  public ReportMovieFailureCommand {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(correlationId, "correlationId must not be null");
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(reason, "reason must not be null");
  }
  
  public static ReportMovieFailureCommand of(
      MessageId id, CorrelationId correlationId, TmdbId tmdbId, String reason) {
    return new ReportMovieFailureCommand(id, correlationId, tmdbId, reason);
  }
}
