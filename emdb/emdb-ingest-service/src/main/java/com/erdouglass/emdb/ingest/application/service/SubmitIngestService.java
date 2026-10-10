package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestCommandOutbox;
import com.erdouglass.emdb.ingest.application.port.out.IngestEventOutbox;
import com.erdouglass.emdb.ingest.application.port.out.IngestJobRepository;
import com.erdouglass.emdb.ingest.domain.event.DomainEvent;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.messaging.IngestCommand;
import com.erdouglass.emdb.shared.kernel.CorrelationId;

@ApplicationScoped
class SubmitIngestService implements SubmitIngestUseCase {
  
  @Inject
  IngestCommandOutbox commands;
  
  @Inject
  IngestEventOutbox events;
  
  @Inject
  IngestJobRepository jobs;

  /// Publish the command to the broker.
  /// 
  /// Commit the [Ingest] job, the [IngestCommand], and the [DomainEvent] in 
  /// the same transaction.
  @Override
  @Transactional
  public IngestId submit(IngestMediaCommand command) {
    var job = Ingest.submit(command.tmdbId(), command.mediaType());
    jobs.save(job);
    commands.save(IngestCommand.builder()
        .id(MessageId.newId())
        .correlationId(CorrelationId.of(job.id().value()))
        .submittedAt(DateTimeFactory.now().toInstant())
        .tmdbId(job.tmdbId())
        .mediaType(job.mediaType())
        .build());
    job.events().forEach(events::save);
    return job.id();
  }
}
