package com.erdouglass.emdb.ingest.adapter.out.messaging;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.out.IngestEventPublisher;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestLoaded;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.messaging.IngestEventMessage;
import com.erdouglass.emdb.ingest.messaging.IngestEventMessage.EventType;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@ApplicationScoped
class IngestEventAdapter implements IngestEventPublisher {
  
  @Inject
  @Channel("ingest-events")
  MutinyEmitter<IngestEventMessage> emitter;

  @Override
  public void publish(IngestEvent event) {
    var type = switch (event) {
      case IngestSubmitted _ -> EventType.SUBMITTED;
      case IngestStarted   _ -> EventType.STARTED;
      case IngestExtracted _ -> EventType.EXTRACTED;
      case IngestLoaded    _ -> EventType.LOADED;
    };
    var message = IngestEventMessage.builder()
        .id(UUID.randomUUID())
        .ingestId(event.ingestId().value())
        .occurredAt(DateTimeFactory.now().toInstant())
        .tmdbId(event.tmdbId().value())
        .mediaType(event.mediaType().toString())
        .eventType(type)
        .build();
    var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
        .withKey(event.ingestId().value())
        .build();    
    emitter.sendMessageAndAwait(Message.of(message).addMetadata(metadata));    
  }
}
