package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.message.IngestMessage;
import com.erdouglass.emdb.common.message.MovieCreateMessage;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto;

import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.baggage.Baggage;
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
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
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
  @Outgoing("movie-create-out")
  public Message<MovieCreateMessage> onMessage(IngestMessage message) {
    var jobId = Baggage.current().getEntryValue("job-id");
    LOGGER.infof("Received: %s for TMDB movie %d", jobId, message.tmdbId());    
    logQueueTime(message.tmdbId());    
    var timer = Timer.builder("emdb.method.duration")
        .description("Measures the time to scrape a movie from TMDB")
        .tag("class", "TmdbMovieScraper") 
        .tag("method", "Movie Scrape")
        .register(registry);
    var createMessage = timer.record(() -> scrape(message.tmdbId()));
    LOGGER.infof("Sent: %s for TMDB movie %d", jobId, message.tmdbId());
    return Message.of(createMessage)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withRoutingKey(CREATE_KEY)
            .build());
  }
  
  private TmdbMovieDto findMovie(int tmdbId) {
    long startTime = System.nanoTime();
    var movie = client.findById(tmdbId, CREDITS);
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var violations = validator.validate(movie);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
    LOGGER.infof("Fetched TMDB movie %d in %d ms", movie.id(), et);
    return movie;
  }
  
  private void logQueueTime(int tmdbId) {
    var start = Instant.parse(Baggage.current().getEntryValue("job-start-time"));
    var et = Duration.between(start, Instant.now());
    LOGGER.infof("TMDB movie %d queued for %d ms", tmdbId, et.toMillis());
  }
  
  private MovieCreateMessage scrape(int tmdbId) {
    var movie = findMovie(tmdbId);
    return MovieCreateMessage.builder()
        .tmdbId(movie.id())
        .title(movie.title())
        .releaseDate(movie.release_date())
        .score(movie.vote_average())
        .status(movie.status())
        .runtime(movie.runtime())
        .budget(movie.budget())
        .revenue(movie.revenue())
        .homepage(movie.homepage())
        .originalLanguage(movie.original_language())
        .backdrop(movie.backdrop_path())
        .poster(movie.poster_path())
        .tagline(movie.tagline())
        .overview(movie.overview())        
        .build();
  }

}
