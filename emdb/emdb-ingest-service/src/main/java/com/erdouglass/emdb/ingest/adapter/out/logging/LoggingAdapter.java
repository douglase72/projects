package com.erdouglass.emdb.ingest.adapter.out.logging;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestFailed;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;

@ApplicationScoped
class LoggingAdapter {
  private static final Logger LOGGER = Logger.getLogger(LoggingAdapter.class);
  
  void onSubmitted(@Observes IngestSubmitted event) {
    LOGGER.infof("Ingest job for TMDB %s %s submitted.", event.type(), event.tmdbId().value());
  }
  
  void onStarted(@Observes IngestStarted event) {
    var et = Duration.between(event.submittedAt().toInstant(), Instant.now()).toMillis();   
    LOGGER.infof("Ingest job for TMDB %s %s started after sitting in the queue for %d ms.",
        event.type(), event.tmdbId().value(), et);
  }
  
  void onExtracted(@Observes IngestExtracted event) {
    LOGGER.infof("Ingest job for TMDB %s %s extracted.", event.type(), event.tmdbId().value());
  }  
  
  void onFailed(@Observes IngestFailed event) {
    LOGGER.errorf("Ingest job for TMDB %s %s failed: %s", 
        event.type(), event.tmdbId().value(), event.cause());
  }
}
