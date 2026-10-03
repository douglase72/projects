package com.erdouglass.emdb.scraper.application.port.in;

import java.time.Instant;
import java.util.UUID;

import com.erdouglass.emdb.shared.kernel.TmdbId;

public interface ScrapeMovieUseCase {

  void scrape(UUID ingestId, TmdbId tmdbId, Instant submittedAt);
}
