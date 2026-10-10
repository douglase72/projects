package com.erdouglass.emdb.scraper.adapter.out.messaging;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.messaging.IngestEvent;
import com.erdouglass.emdb.ingest.messaging.IngestEvent.EventType;
import com.erdouglass.emdb.scraper.application.port.out.MovieEventEmitter;
import com.erdouglass.emdb.scraper.domain.event.MovieEvent;
import com.erdouglass.emdb.scraper.domain.event.MovieExtracted;
import com.erdouglass.emdb.scraper.domain.event.MovieStarted;
import com.erdouglass.emdb.scraper.messaging.MovieScraped;
import com.erdouglass.emdb.shared.kernel.MediaType;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@ApplicationScoped
class MovieEventProducer implements MovieEventEmitter {
  private static final Logger LOGGER = Logger.getLogger(MovieEventProducer.class);
  
  @Inject
  @Channel("ingest-events")
  MutinyEmitter<IngestEvent> emitter;
  
  @Inject
  @Channel("movies-scraped")
  MutinyEmitter<MovieScraped> movies;

  @Override
  public void emit(MovieEvent event) {
    switch (event) {
      case MovieStarted   e -> publishStartedEvent(e);
      case MovieExtracted e -> { publishScrapedEvent(e); publishExtractedEvent(e); }
    }
  }
  
  private void publishExtractedEvent(MovieExtracted event) {
    var message = IngestEvent.builder()
        .id(event.id())
        .correlationId(event.correlationId())
        .occurredAt(DateTimeFactory.now().toInstant())
        .tmdbId(event.details().tmdbId())
        .mediaType(MediaType.MOVIE)
        .eventType(EventType.EXTRACTED)
        .build();
    var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
        .withKey(event.correlationId().value())
        .build();    
    emitter.sendMessageAndAwait(Message.of(message).addMetadata(metadata));
    LOGGER.debugf("Published: %s", message);
  }
  
  private void publishScrapedEvent(MovieExtracted event) {
    var movie = event.details();
    var message = MovieScraped.builder()
        .id(event.id())
        .correlationId(event.correlationId())
        .tmdbId(movie.tmdbId())
        .title(movie.title())
        .releaseDate(movie.releaseDate())
        .score(movie.score())
        .originalLanguage(movie.originalLanguage())
        .overview(movie.overview())
        .build();
    var metadata = OutgoingKafkaRecordMetadata.<Integer>builder()
        .withKey(movie.tmdbId().value())
        .build();    
    movies.sendMessageAndAwait(Message.of(message).addMetadata(metadata));
    LOGGER.debugf("Published: %s", message);
  }
  
  private void publishStartedEvent(MovieStarted event) {
    var message = IngestEvent.builder()
        .id(event.id())
        .correlationId(event.correlationId())
        .occurredAt(DateTimeFactory.now().toInstant())
        .tmdbId(event.tmdbId())
        .mediaType(MediaType.MOVIE)
        .eventType(EventType.STARTED)
        .build();
    var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
        .withKey(event.correlationId().value())
        .build();    
    emitter.sendMessageAndAwait(Message.of(message).addMetadata(metadata));
    LOGGER.debugf("Published: %s", message);
  }
}
