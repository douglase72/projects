package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.out.IngestPublisher;

@ApplicationScoped
class IngestPublisherAdapter implements IngestPublisher {
  
  @Inject
  @Channel("ingest-media-out")
  Emitter<IngestMediaCommand> emitter;

  @Override
  public void publish(IngestMediaCommand command) {
    emitter.send(Message.of(command));
  }
}
