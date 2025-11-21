package com.erdouglass.emdb.scraper.producer;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.scraper.dto.TmdbMovieMessage;
import com.erdouglass.emdb.scraper.dto.TmdbMovieMessage.MessageType;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class TmdbMovieProducer {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieProducer.class);
  
  @Inject
  @Channel("tmdb-movies") 
  Emitter<TmdbMovieMessage> emitter;
  
  @Scheduled(cron = "0 0 0 * * ?")
  public void cron() {
    // Get the movies that have changed in the last 24 hours from TMDB.
    var tmdbIds = List.of(78, 1061474, 816, 817);
    
    for(var tmdbId : tmdbIds) {
      var message = TmdbMovieMessage.of(tmdbId, MessageType.INGEST);
      emitter.send(message);
      LOGGER.infof("Queued message: %s", message);
    }
  }

  public void ingest(@NotNull @Positive Integer tmdbId) {
    var message = TmdbMovieMessage.of(tmdbId, MessageType.INGEST);
    emitter.send(message);
    LOGGER.infof("Queued message: %s", message);
  }
  
  public void synchronize(@NotNull @Positive Long emdbId, @NotNull @Positive Integer tmdbId) {
    var message = TmdbMovieMessage.of(emdbId, tmdbId, MessageType.SYNCHRONIZE);
    emitter.send(message);
    LOGGER.infof("Queued message: %s", message);
  }
  
}
