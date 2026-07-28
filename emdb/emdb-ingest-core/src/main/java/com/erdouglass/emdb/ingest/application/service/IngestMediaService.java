package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.inbound.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.inbound.IngestMediaUseCase;
import com.erdouglass.emdb.ingest.application.port.outbound.IngestProducer;
import com.erdouglass.emdb.ingest.application.port.outbound.IngestRepository;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

@ApplicationScoped
public class IngestMediaService implements IngestMediaUseCase {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  
  @Inject
  IngestProducer producer;
  
  @Inject
  IngestRepository repository;

  @Override
  public IngestId ingest(IngestMediaCommand command) {
    var ingest = Ingest.submit(IngestId.of(GENERATOR.generate()), command.sourceId(), command.mediaType());
    repository.save(ingest);
    producer.publish(ingest.id());
    return ingest.id();
  }
}
