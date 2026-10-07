package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.ingest.application.port.in.SaveIngestEventUseCase;
import com.erdouglass.emdb.ingest.application.port.out.EventRepository;
import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.DomainEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestCompleted;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.exception.IngestNotFoundException;

@ApplicationScoped
class PersistIngestService implements SaveIngestEventUseCase {
  
  @Inject
  Event<DomainEvent> emitter;
  
  @Inject
  EventRepository events;
  
  @Inject
  IngestRepository jobs;

  @Override
  @Transactional
  public void save(DomainEvent event) {
    if (!events.insertIfAbsent(event)) {
      return;
    }
    var job = jobs.findById(event.ingestId())
        .orElseThrow(() -> new IngestNotFoundException(event.ingestId()));
    switch (event) {
      case IngestSubmitted _ -> { }
      case IngestStarted   _ -> { job.start(); jobs.save(job); }
      case IngestExtracted _ -> { job.extract(); jobs.save(job); }
      case IngestCompleted _ -> { job.complete(); jobs.save(job); }
    }
  }
}
