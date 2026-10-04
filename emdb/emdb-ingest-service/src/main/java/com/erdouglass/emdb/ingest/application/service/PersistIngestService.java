package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.in.SaveIngestEventUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestLoaded;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.exception.IngestNotFoundException;

@ApplicationScoped
class PersistIngestService implements SaveIngestEventUseCase {
  
  @Inject
  Event<IngestEvent> emitter;
  
  @Inject
  IngestRepository jobs;
  
  @Override
  public void save(IngestEvent event) {
    var job = jobs.findById(event.ingestId())
        .orElseThrow(() -> new IngestNotFoundException(event.ingestId()));
    switch (event) {
      case IngestSubmitted _ -> { }
      case IngestStarted   _ -> { job.start(); jobs.save(job); }
      case IngestExtracted _ -> { job.extract(); jobs.save(job); }
      case IngestLoaded    _ -> { job.load(); jobs.save(job); }
    }
    job.pullEvents().forEach(emitter::fire);
  }
}
