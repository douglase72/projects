package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestCommandQueue;
import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmittedEvent;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

@ApplicationScoped
class SubmitIngestService implements SubmitIngestUseCase {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  
  @Inject
  Event<IngestSubmittedEvent> emitter;
  
  @Inject
  IngestCommandQueue queue;
  
  @Inject
  IngestRepository repository;

  @Override
  public IngestId submit(IngestMediaCommand command) {
    var id = IngestId.of(GENERATOR.generate());
    var ingest = Ingest.submit(id, command.tmdbId(), command.type());
    repository.save(ingest);
    queue.enqueue(id);
    return id;
  }
}
