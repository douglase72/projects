package com.erdouglass.emdb.scraper.application.port.out;

import com.erdouglass.emdb.scraper.domain.model.MovieDetails;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public interface MovieScraper {
  
  MovieDetails scrape(TmdbId tmdbId);
}
