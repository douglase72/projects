package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestCommandRepository;
import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.messaging.IngestCommand;
import com.erdouglass.emdb.shared.kernel.CorrelationId;

@ApplicationScoped
class SubmitIngestService implements SubmitIngestUseCase {
  
  @Inject
  IngestCommandRepository commands;
  
  @Inject
  IngestRepository jobs;

  /// Publish the command to the broker.
  /// 
  /// Commit the [Ingest] job and the [IngestCommand] in the same transaction
  /// to avoid a duel write.
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
    return job.id();
  }
}
