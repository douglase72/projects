package com.erdouglass.emdb.scraper.application.port.in;

import java.time.Instant;
import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.shared.kernel.PublicId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record ScrapeMediaCommand(
    MessageId messageId, 
    PublicId correlationId, 
    TmdbId tmdbId, 
    Instant submittedAt) {

  public ScrapeMediaCommand {
    Objects.requireNonNull(messageId, "messageId is required");
    Objects.requireNonNull(correlationId, "correlationId is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(submittedAt, "submittedAt is required");
  }
  
  public static ScrapeMediaCommand of(
      MessageId messageId, PublicId ingestId, TmdbId tmdbId, Instant submittedAt) {
    return new ScrapeMediaCommand(messageId, ingestId, tmdbId, submittedAt);
  }
}