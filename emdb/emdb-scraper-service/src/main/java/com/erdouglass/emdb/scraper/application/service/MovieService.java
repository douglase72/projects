package com.erdouglass.emdb.scraper.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.scraper.application.port.in.ScrapeMediaCommand;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMovieUseCase;
import com.erdouglass.emdb.scraper.application.port.out.CommandRepository;
import com.erdouglass.emdb.scraper.application.port.out.MovieEventEmitter;
import com.erdouglass.emdb.scraper.application.port.out.MovieScraper;
import com.erdouglass.emdb.scraper.domain.model.Movie;

@ApplicationScoped
class MovieService implements ScrapeMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  CommandRepository commands;
  
  @Inject
  MovieEventEmitter emitter;
  
  @Inject
  MovieScraper scraper;

  @Override
  public void scrape(ScrapeMediaCommand command) {
    if (commands.hasProcessed(command.id())) {
      return;
    }
    
    // Publish MovieStarted event
    var movie = Movie.create(command.correlationId(), command.tmdbId());
    movie.pullEvents().forEach(emitter::emit);
    
    // Publish MovieExtracted event
    var details = scraper.scrape(command.tmdbId());
    movie.extract(details);
    LOGGER.infof("Scraped %s for TMDB id: %d", movie.title(), movie.tmdbId().value());
    movie.pullEvents().forEach(emitter::emit);
    
    commands.markProcessed(command.id());
  }
}
