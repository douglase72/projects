package com.erdouglass.emdb.ingest.adapter.outbound.observability;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.domain.event.IngestCompletedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestExtractedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestFailedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestLoadedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestStartedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmittedEvent;

@ApplicationScoped
class LoggingObserver {
  private static final Logger LOGGER = Logger.getLogger(LoggingObserver.class);
  
  public void onSubmittedEvent(@Observes IngestSubmittedEvent event) {
    LOGGER.info(event.message());
  }  

  public void onStartedEvent(@Observes IngestStartedEvent event) {
    LOGGER.info(event.message());
  }
  
  public void onExtractedEvent(@Observes IngestExtractedEvent event) {
    LOGGER.info(event.message());
  } 
  
  public void onLoadedEvent(@Observes IngestLoadedEvent event) {
    LOGGER.info(event.message());
  }   
  
  public void onCompletedEvent(@Observes IngestCompletedEvent event) {
    LOGGER.info(event.message());
  }   
  
  public void onFailedEvent(@Observes IngestFailedEvent event) {
    LOGGER.error(event.message());
  }   
}
