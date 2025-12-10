package com.erdouglass.emdb.scraper.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.IngestCommand;

import io.smallrye.reactive.messaging.annotations.Blocking;

@ApplicationScoped
public class TmdbMovieScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  
  @Blocking
  @Incoming("movie-ingest-in")
  public void onMessage(@NotNull @Valid IngestCommand message) {
    LOGGER.infof("Received: %s", message);
  }

}
