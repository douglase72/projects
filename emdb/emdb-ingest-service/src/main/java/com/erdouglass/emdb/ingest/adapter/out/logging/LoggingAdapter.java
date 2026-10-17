package com.erdouglass.emdb.ingest.adapter.out.logging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.domain.event.IngestCompleted;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestFailed;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;

@ApplicationScoped
class LoggingAdapter {
  private static final Logger LOGGER = Logger.getLogger(LoggingAdapter.class);
  
  void onSubmitted(@Observes(during = TransactionPhase.AFTER_SUCCESS) IngestSubmitted event) {
    LOGGER.infof("Ingest job for TMDB %s %s submitted", event.mediaType(), event.tmdbId().value());
  }
  
  void onExtracted(@Observes(during = TransactionPhase.AFTER_SUCCESS) IngestExtracted event) {
    LOGGER.debugf("Ingest job for TMDB %s %s extracted", event.mediaType(), event.tmdbId().value());
  }
  
  void onCompleted(@Observes(during = TransactionPhase.AFTER_SUCCESS) IngestCompleted event) {
    LOGGER.infof("Ingest job for TMDB %s %s completed", event.mediaType(), event.tmdbId().value());
  }
  
  void onFailed(@Observes(during = TransactionPhase.AFTER_SUCCESS) IngestFailed event) {
    LOGGER.errorf("Ingest job for TMDB %s %s failed", event.mediaType(), event.tmdbId().value());
  }  
}
