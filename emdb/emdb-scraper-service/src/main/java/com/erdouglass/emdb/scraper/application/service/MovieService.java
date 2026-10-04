package com.erdouglass.emdb.scraper.application.service;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.messaging.IngestEventMessage;
import com.erdouglass.emdb.ingest.messaging.IngestEventMessage.EventType;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMediaCommand;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMovieUseCase;
import com.erdouglass.emdb.scraper.application.port.out.EventPublisher;
import com.erdouglass.emdb.scraper.application.port.out.MoviePublisher;
import com.erdouglass.emdb.scraper.application.port.out.MovieScraper;
import com.erdouglass.emdb.shared.kernel.MediaType;

@ApplicationScoped
class MovieService implements ScrapeMovieUseCase {
  
  @Inject
  EventPublisher events;
  
  @Inject
  MoviePublisher movies;
  
  @Inject
  MovieScraper scraper;

  @Override
  public void scrape(ScrapeMediaCommand command) {
    var event = IngestEventMessage.builder()
        .id(UUID.randomUUID())
        .ingestId(command.ingestId())
        .occurredAt(DateTimeFactory.now().toInstant())
        .tmdbId(command.tmdbId().value())
        .mediaType(MediaType.MOVIE.toString()) 
        .eventType(EventType.STARTED)
        .build();
    events.publish(event);
    
    var movie = scraper.scrape(command.tmdbId());
    movies.publish(command.ingestId(), movie);   
    event = IngestEventMessage.builder()
        .id(UUID.randomUUID())
        .ingestId(command.ingestId())
        .occurredAt(DateTimeFactory.now().toInstant())
        .tmdbId(command.tmdbId().value())
        .mediaType(MediaType.MOVIE.toString()) 
        .eventType(EventType.EXTRACTED)
        .build();
    events.publish(event);
  }
}
