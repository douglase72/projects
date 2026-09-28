package com.erdouglass.emdb.scraper.application.port.in;

import com.erdouglass.emdb.shared.kernel.TmdbId;

public interface ScrapeMovieUseCase {

  void scrape(TmdbId tmdbId);
}
