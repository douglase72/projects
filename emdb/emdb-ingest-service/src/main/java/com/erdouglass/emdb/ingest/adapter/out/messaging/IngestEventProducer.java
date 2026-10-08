package com.erdouglass.emdb.ingest.adapter.out.messaging;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.adapter.out.persistence.JakartaDataIngestEventRepository;
import com.erdouglass.emdb.ingest.adapter.out.persistence.JakartaDataIngestRepository;
import com.erdouglass.emdb.ingest.messaging.IngestEvent;
import com.erdouglass.emdb.ingest.messaging.IngestEvent.EventType;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

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
  
  @Inject
  JakartaDataIngestRepository jobs;
  
  @Scheduled(
      every = "{event.outbox.interval}", 
      delayed = "{event.outbox.delay}",
      concurrentExecution = ConcurrentExecution.SKIP)
  void publish() {    
    for (var entity : events.findUnpublished()) {
      var job = jobs.findById(entity.getIngestId()).orElseThrow();
      var type = switch (entity.getStatus()) {
        case SUBMITTED -> EventType.SUBMITTED;
        case STARTED   -> EventType.STARTED;
        case EXTRACTED -> EventType.EXTRACTED;
        case COMPLETED -> EventType.COMPLETED;
        case FAILED    -> EventType.FAILED;
      };      
      var event = IngestEvent.builder()
          .messageId(MessageId.of(entity.getId()))
          .correlationId(CorrelationId.of(entity.getIngestId()))
          .occurredAt(DateTimeFactory.now().toInstant())
          .tmdbId(TmdbId.of(job.getTmdbId()))
          .mediaType(job.getMediaType())
          .eventType(type)
          .build();
      var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
          .withKey(entity.getIngestId())
          .build();    
      emitter.sendMessageAndAwait(Message.of(event).addMetadata(metadata));
      
      entity.setPublished(true);
      events.update(entity); 
    }
  }
}
