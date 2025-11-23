package com.erdouglass.emdb.scraper.producer;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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

@ApplicationScoped
public class TmdbMovieProducer {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieProducer.class);
  
  @Inject
  @Channel("movie-request-out") 
  Emitter<MovieStatus> emitter;
  
  @Scheduled(cron = "0 0 0 * * ?")
  public void cron() {
    // Get the movies that have changed in the last 24 hours from TMDB.
    var tmdbIds = List.of(78, 816, 1061474, 817, 818);
    
    // Scrape the TMDB movie
    for (var tmdbId : tmdbIds) {
      ingest(tmdbId);
    }
  }

  public void ingest(@NotNull @Positive Integer tmdbId) {
    var message = MovieStatus.of(tmdbId, MessageType.INGEST, MessageStatus.QUEUED);
    LOGGER.infof("Status: %s", message);
    emitter.send(message);
  }
  
  @RunOnVirtualThread
  @Incoming("movie-status-in")
  public void status(JsonObject jsonObject) throws InterruptedException {
    MovieStatus message = jsonObject.mapTo(MovieStatus.class);
    LOGGER.infof("Status: %s", message);
  }
  
}
