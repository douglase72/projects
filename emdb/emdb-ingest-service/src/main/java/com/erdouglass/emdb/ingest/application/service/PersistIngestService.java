package com.erdouglass.emdb.ingest.application.service;

import java.time.Duration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.in.SaveIngestEventUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestEventRepository;
import com.erdouglass.emdb.ingest.application.port.out.IngestJobRepository;
import com.erdouglass.emdb.ingest.domain.event.DomainEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestCompleted;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestFailed;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.exception.IngestNotFoundException;
import com.erdouglass.emdb.ingest.domain.model.Ingest;

@ApplicationScoped
class PersistIngestService implements SaveIngestEventUseCase {
  private static final Logger LOGGER = Logger.getLogger(PersistIngestService.class);
  
  @Inject
  IngestEventRepository events;
  
  @Inject
  IngestJobRepository jobs;

  @Override
  @Transactional
  public void save(DomainEvent event) {    
    if (events.existsById(event.id())) {
      return;
    }
    var job = jobs.findById(event.ingestId())
        .orElseThrow(() -> new IngestNotFoundException(event.ingestId()));
    switch (event) {
      case IngestSubmitted _ -> { }
      case IngestStarted   _ -> { job.start();    jobs.save(job); }
      case IngestExtracted _ -> { job.extract();  jobs.save(job); logExtracted(job); }
      case IngestCompleted _ -> { job.complete(); jobs.save(job); logCompleted(job); }
      case IngestFailed    _ -> { job.failed();   jobs.save(job); logFailed(job); }
    }
  }
  
  private void logCompleted(Ingest job) {
    var et = Duration.between(job.submittedAt().toInstant(), DateTimeFactory.now().toInstant()).toMillis();
    LOGGER.infof("Ingest job for TMDB %s %s completed in %d ms", job.mediaType(), job.tmdbId().value(), et);
  }
  
  private void logExtracted(Ingest job) {
    LOGGER.debugf("Ingest job for TMDB %s %s extracted", job.mediaType(), job.tmdbId().value());
  }
  
  private void logFailed(Ingest job) {
    LOGGER.errorf("Ingest job for TMDB %s %s failed", job.mediaType(), job.tmdbId().value());
  }
}
