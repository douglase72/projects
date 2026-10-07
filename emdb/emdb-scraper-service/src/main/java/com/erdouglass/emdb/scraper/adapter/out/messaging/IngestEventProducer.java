package com.erdouglass.emdb.scraper.adapter.out.messaging;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.messaging.IngestEvent;
import com.erdouglass.emdb.scraper.application.port.out.IngestEventPublisher;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

/// Publishes ingest events to the broker.
/// 
/// Publish the [IngestEvent] to the emdb.ingest.events Kafka topic keyed by 
/// the ingest job id so that multiple events for the same job maintain their order.
@ApplicationScoped
class IngestEventProducer implements IngestEventPublisher {
  
  @Inject
  @Channel("ingest-events")
  MutinyEmitter<IngestEvent> emitter;

  @Override
  public void publish(IngestEvent event) {
    var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
        .withKey(event.correlationId().value())
        .build();    
    emitter.sendMessageAndAwait(Message.of(event).addMetadata(metadata));
  }
}
