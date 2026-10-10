package com.erdouglass.emdb.scraper.application.port.in;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Builder;

@Builder
public record ScrapeMediaCommand(
    MessageId id, 
    CorrelationId correlationId, 
    TmdbId tmdbId, 
    Instant submittedAt) {

  public ScrapeMediaCommand {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(correlationId, "correlationId is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(submittedAt, "submittedAt is required");
  }
}
