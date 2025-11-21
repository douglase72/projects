package com.erdouglass.emdb.scraper.producer;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.scraper.dto.TmdbMovieMessage;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class TmdbMovieProcessor {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieProcessor.class);
  
  @Blocking
  @Incoming("movies")
  public void scrape(JsonObject jsonObject) throws InterruptedException {
    TmdbMovieMessage message = jsonObject.mapTo(TmdbMovieMessage.class);
    LOGGER.infof("Received message: %s", message);
    switch (message.type()) {
      case INGEST -> ingest(message.tmdbId());
      case SYNCHRONIZE -> synchronize(message.emdbId(), message.tmdbId());
    }
  }
  
  private void ingest(int tmdbId) throws InterruptedException {
    LOGGER.infof("Ingesting TMDB movie id: %d", tmdbId);
    
    // Simulate scraping the movie
    Thread.sleep(10000);
    
    LOGGER.infof("Completed ingesting TMDB movie id: %d", tmdbId);
  }
  
  private void synchronize(long emdbId, int tmdbId) throws InterruptedException {
    LOGGER.infof("Synchronizing EMDB movie id: %d with TMDB movie id: %d", emdbId,  tmdbId);
    
    // Simulate scraping the movie
    Thread.sleep(10000);
    
    LOGGER.infof("Completed synchronizing EMDB movie id: %d with TMDB movie id: %d", emdbId,  tmdbId);
  }

}
