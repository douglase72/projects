package com.erdouglass.emdb.gateway.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  public void ingest(@NotNull @Positive Integer tmdbId) {
    LOGGER.infof("Queued ingest job for TMDB movie: %d", tmdbId);
  }

}
