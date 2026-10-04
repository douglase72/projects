package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;

import com.erdouglass.emdb.ingest.application.port.out.IngestCommandPublisher;
import com.erdouglass.emdb.ingest.messaging.IngestMediaMessage;

import io.smallrye.reactive.messaging.MutinyEmitter;

@ApplicationScoped
class IngestCommandAdapter implements IngestCommandPublisher {
  
  @Inject
  @Channel("ingest-media")
  MutinyEmitter<IngestMediaMessage> emitter;

  /// Publish the message to the RabbitMQ broker.
  @Override
  public void publish(IngestMediaMessage message) {
    emitter.sendAndAwait(message);
  }
}
