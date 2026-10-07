package com.erdouglass.emdb.scraper.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.messaging.IngestEvent;
import com.erdouglass.emdb.ingest.messaging.IngestEvent.EventType;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMediaCommand;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMovieUseCase;
import com.erdouglass.emdb.scraper.application.port.out.IngestEventPublisher;
import com.erdouglass.emdb.scraper.application.port.out.MoviePublisher;
import com.erdouglass.emdb.scraper.application.port.out.MovieScraper;
import com.erdouglass.emdb.shared.kernel.MediaType;

@ApplicationScoped
class MovieService implements ScrapeMovieUseCase {
  
  @Inject
  IngestEventPublisher events;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MoviePublisher movies;
  
  @Inject
  MovieScraper scraper;

  @Override
  public void scrape(ScrapeMediaCommand command) {
    var event = IngestEvent.builder()
        .messageId(MessageId.newId())
        .correlationId(command.correlationId())
        .occurredAt(DateTimeFactory.now().toInstant())
        .tmdbId(command.tmdbId())
        .mediaType(MediaType.MOVIE) 
        .eventType(EventType.STARTED)
        .build();
    events.publish(event);
    
    var movie = scraper.scrape(command.tmdbId());
    movies.publish(mapper.toMovieExtracted(command.messageId(), command.correlationId(), movie));
    
    event = IngestEvent.builder()
        .messageId(MessageId.newId())
        .correlationId(command.correlationId())
        .occurredAt(DateTimeFactory.now().toInstant())
        .tmdbId(command.tmdbId())
        .mediaType(MediaType.MOVIE) 
        .eventType(EventType.EXTRACTED)
        .build();
    events.publish(event);
  }
}
