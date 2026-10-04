package com.erdouglass.emdb.ingest.adapter.out.logging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestLoaded;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;

@ApplicationScoped
class LoggingAdapter {
  private static final Logger LOGGER    = Logger.getLogger(LoggingAdapter.class);
  private static final String SUBMITTED = "Ingest job %s for TMDB %s %s submitted.";
  private static final String STARTED   = "Ingest job %s for TMDB %s %s started after being queued for %d ms.";
  private static final String EXTRACTED = "Ingest job %s for TMDB %s %s extracted.";
  private static final String LOADED    = "Ingest job %s for TMDB %s %s loaded.";
  
  void onEvent(@Observes IngestEvent event) {
    switch (event) {
      case IngestSubmitted e -> LOGGER.infof(SUBMITTED, e.ingestId().value(), e.mediaType(), e.tmdbId().value());
      case IngestStarted   e -> LOGGER.infof(STARTED, 
          e.ingestId().value(), e.mediaType(), e.tmdbId().value(), e.queued().toMillis());
      case IngestExtracted e -> LOGGER.infof(EXTRACTED, e.ingestId().value(), e.mediaType(), e.tmdbId().value());
      case IngestLoaded    e -> LOGGER.infof(LOADED, e.ingestId().value(), e.mediaType(), e.tmdbId().value());
    };
  }
}
