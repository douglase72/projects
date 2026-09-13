package com.erdouglass.emdb.ingest.adapter.in.messaging;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.IngestMediaUseCase;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
class IngestConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestConsumer.class);
  
  @Inject
  IngestMediaUseCase ingestUseCase;

  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  CompletionStage<Void> onMessage(Message<IngestMediaCommand> message) {
    try {
      ingestUseCase.ingest(message.getPayload());
      return message.ack();
    } catch (Exception e) {
      LOGGER.errorf(e , "Failed to ingest media");
      return message.nack(e);
    }
  }
}
