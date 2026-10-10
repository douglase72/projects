package com.erdouglass.emdb.ingest.adapter.out.messaging;

import java.util.UUID;

import jakarta.data.Limit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.ingest.adapter.out.persistence.JakartaDataEventOutboxRepository;
import com.erdouglass.emdb.ingest.messaging.IngestEvent;
import com.erdouglass.emdb.ingest.messaging.IngestEvent.EventType;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@ApplicationScoped
class IngestEventOutbox {

  @Inject
  @Channel("ingest-events-out")
  MutinyEmitter<IngestEvent> emitter;
  
  @Inject
  JakartaDataEventOutboxRepository events;
  
  @Scheduled(
      every = "{event.outbox.interval}", 
      delayed = "{event.outbox.delay}",
      concurrentExecution = ConcurrentExecution.SKIP)
  void publish() {
    for (var event : events.findAll(Limit.of(100))) {
      var type = switch (event.getStatus()) {
        case SUBMITTED -> EventType.SUBMITTED;
        case STARTED   -> EventType.STARTED;
        case EXTRACTED -> EventType.EXTRACTED;
        case COMPLETED -> EventType.COMPLETED;
        case FAILED    -> EventType.FAILED;
      };
      var message = IngestEvent.builder()
          .id(MessageId.of(event.getId()))
          .correlationId(CorrelationId.of(event.getIngestId()))
          .occurredAt(event.getOccurredAt())
          .tmdbId(TmdbId.of(event.getTmdbId()))
          .mediaType(event.getMediaType())
          .eventType(type)
          .build();
      var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
          .withKey(event.getIngestId())
          .build();    
      emitter.sendMessageAndAwait(Message.of(message).addMetadata(metadata));
      QuarkusTransaction.requiringNew().run(() -> events.delete(event));       
    }
  }
}
