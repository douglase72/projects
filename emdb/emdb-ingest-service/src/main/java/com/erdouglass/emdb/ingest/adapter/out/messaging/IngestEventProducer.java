package com.erdouglass.emdb.ingest.adapter.out.messaging;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.out.IngestEventPublisher;
import com.erdouglass.emdb.ingest.domain.event.DomainEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestCompleted;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.messaging.IngestEvent;
import com.erdouglass.emdb.ingest.messaging.IngestEvent.EventType;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

/// Publishes ingest events to the broker.
/// 
/// Create the [IngestEvent] from the domain event and publish to the 
/// emdb.ingest.events Kafka topic keyed by the ingest job id so that multiple
/// events for the same job maintain their order.
@ApplicationScoped
class IngestEventProducer implements IngestEventPublisher {
  
  @Inject
  @Channel("ingest-events-out")
  MutinyEmitter<IngestEvent> emitter;

  @Override
  public void publish(DomainEvent event) {
    var type = switch (event) {
      case IngestSubmitted _ -> EventType.SUBMITTED;
      case IngestStarted   _ -> EventType.STARTED;
      case IngestExtracted _ -> EventType.EXTRACTED;
      case IngestCompleted _ -> EventType.COMPLETED;
    };
    var message = IngestEvent.builder()
        .messageId(event.messageId())
        .correlationId(event.ingestId())
        .occurredAt(DateTimeFactory.now().toInstant())
        .tmdbId(event.tmdbId())
        .mediaType(event.mediaType())
        .eventType(type)
        .build();
    var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
        .withKey(event.ingestId().value())
        .build();    
    emitter.sendMessageAndAwait(Message.of(message).addMetadata(metadata));
  }
}
