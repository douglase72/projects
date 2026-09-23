package com.erdouglass.emdb.ingest.adapter.out.logging;

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
    LOGGER.infof(event.message());
  }
  
  void onStarted(@Observes IngestStarted event) {
    LOGGER.infof(event.message());
  }
  
  void onExtracted(@Observes IngestExtracted event) {
    LOGGER.infof(event.message());
  }  
  
  void onFailed(@Observes IngestFailed event) {
    LOGGER.errorf(event.message());
  }
}
