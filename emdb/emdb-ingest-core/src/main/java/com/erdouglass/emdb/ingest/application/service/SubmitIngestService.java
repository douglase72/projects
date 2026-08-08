package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.inbound.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.inbound.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.outbound.IngestProducer;
import com.erdouglass.emdb.ingest.application.port.outbound.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmittedEvent;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

@ApplicationScoped
class SubmitIngestService implements SubmitIngestUseCase {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  
  @Inject
  Event<IngestEvent> emitter;
  
  @Inject
  IngestProducer producer;
  
  @Inject
  IngestRepository repository;

  @Override
  public IngestId submit(IngestMediaCommand command) {
    var ingest = Ingest.submit(IngestId.of(GENERATOR.generate()), command.tmdbId(), command.type());
    repository.save(ingest);
    emitter.fire(IngestSubmittedEvent.of(ingest.id(), ingest.message()));
    producer.publish(ingest.id());
    return ingest.id();
  }
}
