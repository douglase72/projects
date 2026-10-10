package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.data.Limit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.ingest.adapter.out.persistence.JakartaDataCommandOutboxRepository;
import com.erdouglass.emdb.ingest.messaging.IngestCommand;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;
import io.smallrye.reactive.messaging.MutinyEmitter;

@ApplicationScoped
class IngestCommandOutboxAdapter {

  @Inject
  @Channel("ingest-commands")
  MutinyEmitter<IngestCommand> emitter;
  
  @Inject
  JakartaDataCommandOutboxRepository commands;
  
  @Scheduled(
      every = "{command.outbox.interval}", 
      delayed = "{command.outbox.delay}",
      concurrentExecution = ConcurrentExecution.SKIP)
  void publish() {
    for (var command : commands.findAll(Limit.of(100))) {
      var message = IngestCommand.builder()
          .id(MessageId.of(command.getId()))
          .correlationId(CorrelationId.of(command.getIngestId()))
          .submittedAt(command.getSubmittedAt())
          .tmdbId(TmdbId.of(command.getTmdbId()))
          .mediaType(command.getMediaType())
          .build();  
      emitter.sendAndAwait(message); 
      QuarkusTransaction.requiringNew().run(() -> commands.delete(command));
    }
  }
}
