package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestCommandPublisher;
import com.erdouglass.emdb.ingest.application.port.out.IngestEventPublisher;
import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.DomainEvent;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.messaging.IngestCommand;
import com.erdouglass.emdb.shared.kernel.PublicId;

@ApplicationScoped
class SubmitIngestService implements SubmitIngestUseCase {
  
  @Inject
  Event<DomainEvent> emitter;
  
  @Inject
  IngestCommandPublisher commands;
  
  @Inject
  IngestEventPublisher events;
  
  @Inject
  IngestRepository jobs;

  @Override
  @Transactional
  public PublicId submit(IngestMediaCommand command) {
    var job = Ingest.submit(command.tmdbId(), command.mediaType());
    jobs.save(job);
    job.pullEvents().forEach(emitter::fire);
    return job.id();
  }
  
  void onMessage(@Observes(during = TransactionPhase.AFTER_SUCCESS) DomainEvent event) {
    var command = IngestCommand.builder()
        .messageId(MessageId.newId())
        .correlationId(event.ingestId())
        .tmdbId(event.tmdbId())
        .mediaType(event.mediaType())
        .build();
    commands.publish(command);
    events.publish(event);
  }
}
