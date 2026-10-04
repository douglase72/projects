package com.erdouglass.emdb.scraper.application.port.in;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.erdouglass.emdb.shared.kernel.TmdbId;

public record ScrapeMediaCommand(UUID ingestId, TmdbId tmdbId, Instant submittedAt) {

  public ScrapeMediaCommand {
    Objects.requireNonNull(ingestId, "ingestId is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(submittedAt, "submittedAt is required");
  }
  
  public static ScrapeMediaCommand of(UUID ingestId, TmdbId tmdbId, Instant submittedAt) {
    return new ScrapeMediaCommand(ingestId, tmdbId, submittedAt);
  }
}
