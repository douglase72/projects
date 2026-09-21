package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.application.port.out.IngestPublisher;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

@ApplicationScoped
class IngestPublisherAdapter implements IngestPublisher {
  
  @Inject
  @Channel("ingest-media-out")
  Emitter<IngestId> emitter;

  @Override
  public void publish(IngestId id) {
    emitter.send(Message.of(id));
  }
}
