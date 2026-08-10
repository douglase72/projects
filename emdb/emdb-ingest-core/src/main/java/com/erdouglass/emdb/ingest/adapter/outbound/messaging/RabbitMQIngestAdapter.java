package com.erdouglass.emdb.ingest.adapter.outbound.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.application.port.outbound.IngestCommandQueue;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

@ApplicationScoped
class RabbitMQIngestAdapter implements IngestCommandQueue {
  
  @Inject
  @Channel("ingest-media-out")
  Emitter<IngestId> emitter;
  
  @Override
  public void enqueue(IngestId id) {
    emitter.send(Message.of(id));
  }
}
