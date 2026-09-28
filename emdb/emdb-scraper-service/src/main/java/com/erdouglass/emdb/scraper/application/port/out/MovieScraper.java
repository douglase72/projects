package com.erdouglass.emdb.scraper.application.port.out;

import com.erdouglass.emdb.scraper.domain.model.Movie;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public interface MovieScraper {

  Movie scrape(TmdbId tmdbId);
}
