package com.erdouglass.emdb.ingest.adapter.in.messaging;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.in.IngestMediaUseCase;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
class IngestConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestConsumer.class);
  
  @Inject
  IngestMediaUseCase ingestUseCase;

  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  CompletionStage<Void> onMessage(Message<IngestId> message) {
    var id = message.getPayload();
    
    try {
      ingestUseCase.ingest(id);
      return message.ack();
    } catch (Exception e) {
      LOGGER.errorf(e , "Ingest job %s failed", id.value());
      return message.nack(e);
    }
  }
}
