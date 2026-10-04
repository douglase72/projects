package com.erdouglass.emdb.scraper.adapter.out.messaging;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.messaging.IngestEventMessage;
import com.erdouglass.emdb.scraper.application.port.out.EventPublisher;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@ApplicationScoped
class EventAdapter implements EventPublisher {
  
  @Inject
  @Channel("ingest-events")
  MutinyEmitter<IngestEventMessage> emitter;

  @Override
  public void publish(IngestEventMessage message) {
    var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
        .withKey(message.ingestId())
        .build();
    emitter.sendMessageAndAwait(Message.of(message).addMetadata(metadata));
  }
}
