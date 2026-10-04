package com.erdouglass.emdb.ingest.adapter.out.logging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;

@ApplicationScoped
class LoggingAdapter {
  private static final Logger LOGGER    = Logger.getLogger(LoggingAdapter.class);
  private static final String SUBMITTED = "Ingest job %s for TMDB %s %s submitted.";
  
  void onEvent(@Observes IngestEvent event) {
    switch (event) {
      case IngestSubmitted e -> LOGGER.infof(SUBMITTED, e.ingestId().value(), e.mediaType(), e.tmdbId().value());
    }
  }
}
