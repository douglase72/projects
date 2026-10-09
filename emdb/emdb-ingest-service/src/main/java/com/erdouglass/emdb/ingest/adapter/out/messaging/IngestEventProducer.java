package com.erdouglass.emdb.ingest.adapter.out.messaging;

import java.util.UUID;

import jakarta.data.Limit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.ingest.adapter.out.persistence.JakartaDataIngestEventRepository;
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
class IngestEventProducer {
  
  @Inject
  @Channel("ingest-events-out")
  MutinyEmitter<IngestEvent> emitter;

  @Inject
  JakartaDataIngestEventRepository events;
  
  @Scheduled(
      every = "{event.outbox.interval}", 
      delayed = "{event.outbox.delay}",
      concurrentExecution = ConcurrentExecution.SKIP)
  void publish() {    
    for (var entity : events.findUnpublished(Limit.of(100))) {
      var type = switch (entity.getStatus()) {
        case SUBMITTED -> EventType.SUBMITTED;
        case STARTED   -> EventType.STARTED;
        case EXTRACTED -> EventType.EXTRACTED;
        case COMPLETED -> EventType.COMPLETED;
        case FAILED    -> EventType.FAILED;
      };      
      var event = IngestEvent.builder()
          .id(MessageId.of(entity.getId()))
          .correlationId(CorrelationId.of(entity.getIngestId()))
          .occurredAt(entity.getOccurredAt())
          .tmdbId(TmdbId.of(entity.getTmdbId()))
          .mediaType(entity.getMediaType())
          .eventType(type)
          .build();
      var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
          .withKey(entity.getIngestId())
          .build();    
      emitter.sendMessageAndAwait(Message.of(event).addMetadata(metadata));
      QuarkusTransaction.requiringNew().run(() -> events.markPublished(entity.getId())); 
    }
  }
}
