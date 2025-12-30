package com.erdouglass.emdb.scraper.service;

import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.message.IngestMessage;
import com.erdouglass.emdb.common.message.MovieCreateMessage;

import io.opentelemetry.api.baggage.Baggage;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Scrapes movie data from The Movie Database (TMDB).
///
/// This service starts the movie ingestion process by scraping The Movie Database (TMDB).
/// This is a singleton service to ensure the TMDB rate limit is respected. Multiple 
/// replicas will violate that rate limit.
@ApplicationScoped
public class TmdbMovieScraper extends TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREATE_KEY = "movie.create";
  
  @Inject
  @Channel("movie-create-out") 
  MutinyEmitter<MovieCreateMessage> emitter;
  
  /// Scrape the TMDB movie identified in the given message.
  /// 
  /// This method takes one message at a time from the movie-ingest queue and scrapes
  /// TMDB for the relevant movie data. The @Blocking annotation is used to ensure the
  /// process is performed on a separate platform thread so that the event loop doesn't
  /// get blocked. 
  ///
  /// @param message The {@link IngestMessage} to consume from the movie-ingest queue.
  @Blocking
  @Incoming("movie-ingest-in")
  public Uni<Void> onMessage(IngestMessage message) {
    var jobId = Baggage.current().getEntryValue("job-id");
    LOGGER.infof("Received: %s for TMDB movie %d", jobId, message.tmdbId());
    var start = System.nanoTime();
    
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Scraped TMDB movie %d in %d ms", message.tmdbId(), et);
    
    var createMessage = MovieCreateMessage.builder()
        .tmdbId(message.tmdbId())
        .title("Test Title")
        .build();
    return emitter.sendMessage(Message.of(createMessage)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withRoutingKey(CREATE_KEY)
            .build()))
        .invoke(() -> LOGGER.infof("Sent: %s for TMDB movie %d", jobId, message.tmdbId()));
  }

}
