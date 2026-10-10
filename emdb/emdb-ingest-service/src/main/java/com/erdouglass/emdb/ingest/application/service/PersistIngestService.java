package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.in.SaveIngestEventUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestEventRepository;
import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.DomainEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.exception.IngestNotFoundException;

@ApplicationScoped
class PersistIngestService implements SaveIngestEventUseCase {
  private static final Logger LOGGER = Logger.getLogger(PersistIngestService.class);
  
  @Inject
  IngestEventRepository events;
  
  @Inject
  IngestRepository jobs;

  @Override
  @Transactional
  public void save(DomainEvent event) {
    LOGGER.debugf("Received: %s", event);
    if (events.existsById(event.id())) {
      return;
    }
    var job = jobs.findById(event.ingestId())
        .orElseThrow(() -> new IngestNotFoundException(event.ingestId()));
    switch (event) {
      case IngestSubmitted _ -> { }
      case IngestStarted   _ -> { job.start(); jobs.save(job); }
      case IngestExtracted _ -> { job.extract(); jobs.save(job); }
    }
    LOGGER.debugf("job: %s", job);
  }
}
