package com.erdouglass.emdb.scraper.producer;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.query.MovieStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageType;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.vertx.core.json.JsonObject;

/// A scheduled producer that triggers movie ingestion tasks.
///
/// This bean is responsible for:
/// - Scheduling periodic scraping jobs (via cron).
/// - Sending initial ingestion status messages.
/// - Listening for status updates.
@ApplicationScoped
public class TmdbMovieProducer {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieProducer.class);
  
  @Inject
  @Channel("movie-request-out") 
  Emitter<MovieStatus> emitter;
  
  @Inject
  @ConfigProperty(name = "tmdb.movie.limit")
  Integer movieLimit;
  
  /// Triggered automatically every day at midnight.
  ///
  /// Scrapes a predefined list of TMDB Movie IDs that have changed
  /// or require synchronization.
  @Scheduled(cron = "0 0 0 * * ?")
  public void cron() {
    // Get the movies that have changed in the last 24 hours from TMDB.
    var tmdbIds = findChanges().stream().limit(movieLimit).toList();
    
    // Scrape the TMDB movie
    for (var tmdbId : tmdbIds) {
      ingest(tmdbId);
    }
  }

  /// Manually triggers the ingestion process for a specific movie.
  ///
  /// Sends a `QUEUED` status message to the `movie-request-out` channel.
  ///
  /// @param tmdbId The ID of the movie in The Movie Database (TMDB).
  public void ingest(@NotNull @Positive Integer tmdbId) {
    var message = MovieStatus.of(tmdbId, MessageType.INGEST, MessageStatus.QUEUED);
    LOGGER.infof("Status: %s", message);
    emitter.send(message);
  }
  
  /// Listens for status updates from the `movie-status-in` channel.
  ///
  /// Logs the current status of movies moving through the pipeline.
  ///
  /// @param jsonObject The incoming JSON message containing the status update.
  /// @throws InterruptedException If the virtual thread is interrupted during processing.
  @RunOnVirtualThread
  @Incoming("movie-status-in")
  public void status(JsonObject jsonObject) throws InterruptedException {
    MovieStatus message = jsonObject.mapTo(MovieStatus.class);
    LOGGER.infof("Status: %s", message);
  }
  
  private List<Integer> findChanges() {
    return List.of(78, 816, 1061474, 817, 818);
  }
  
}
