package com.erdouglass.emdb.scraper.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.scraper.application.port.in.ScrapeMovieUseCase;
import com.erdouglass.emdb.scraper.application.port.out.MoviePublisher;
import com.erdouglass.emdb.scraper.application.port.out.MovieScraper;
import com.erdouglass.emdb.shared.kernel.TmdbId;

@ApplicationScoped
class MovieService implements ScrapeMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MoviePublisher publisher;
  
  @Inject
  MovieScraper scraper;

  @Override
  public void scrape(TmdbId tmdbId) {
    var movie = scraper.scrape(tmdbId);
    LOGGER.infof("Extracted: %s", movie);
    
    publisher.publish(movie);
  }
}
