package com.erdouglass.emdb.scraper.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.IngestMessage;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class TmdbMovieScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  
  @Blocking
  @Incoming("movie-ingest-in")
  public void onMessage(JsonObject json) {
    var message = json.mapTo(IngestMessage.class);
    LOGGER.infof("Received: %s", message);
  }

}
