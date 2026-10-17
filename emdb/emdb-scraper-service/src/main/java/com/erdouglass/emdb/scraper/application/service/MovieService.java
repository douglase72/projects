package com.erdouglass.emdb.scraper.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.scraper.application.port.in.ScrapeMediaCommand;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMovieUseCase;
import com.erdouglass.emdb.scraper.application.port.out.CommandInbox;
import com.erdouglass.emdb.scraper.application.port.out.MovieEventProducer;
import com.erdouglass.emdb.scraper.application.port.out.MovieScraper;
import com.erdouglass.emdb.scraper.domain.model.Movie;

@ApplicationScoped
class MovieService implements ScrapeMovieUseCase {
  
  @Inject
  CommandInbox inbox;
  
  @Inject
  MovieEventProducer producer;
  
  @Inject
  MovieScraper scraper;

  @Override
  public void scrape(ScrapeMediaCommand command) {
    if (inbox.hasProcessed(command.id())) {
      return;
    }
    var movie = Movie.create(command.correlationId(), command.tmdbId());
    movie.pullEvents().forEach(producer::send);

    var details = scraper.scrape(command.tmdbId());
    movie.extract(details);
    movie.pullEvents().forEach(producer::send);
    
    inbox.markProcessed(command.id());
  }
}
