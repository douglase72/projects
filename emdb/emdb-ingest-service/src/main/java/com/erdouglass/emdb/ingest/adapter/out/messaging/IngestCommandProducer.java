package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.data.Limit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.ingest.adapter.out.persistence.JakartaDataIngestCommandRepository;
import com.erdouglass.emdb.ingest.messaging.IngestCommand;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;
import io.smallrye.reactive.messaging.MutinyEmitter;

@ApplicationScoped
class IngestCommandProducer {

  @Inject
  @Channel("ingest-commands")
  MutinyEmitter<IngestCommand> emitter;
  
  @Inject
  JakartaDataIngestCommandRepository commands;
  
  @Scheduled(
      every = "{command.outbox.interval}", 
      delayed = "{command.outbox.delay}",
      concurrentExecution = ConcurrentExecution.SKIP)
  void publish() {
    for (var entity : commands.findUnpublished(Limit.of(100))) {
      var command = IngestCommand.builder()
          .id(MessageId.of(entity.getId()))
          .correlationId(CorrelationId.of(entity.getIngestId()))
          .submittedAt(entity.getSubmittedAt())
          .tmdbId(TmdbId.of(entity.getTmdbId()))
          .mediaType(entity.getMediaType())
          .build();  
      emitter.sendAndAwait(command); 
      QuarkusTransaction.requiringNew().run(() -> commands.markPublished(entity.getId()));
    }
  }
}
