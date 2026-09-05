package com.erdouglass.emdb.ingest.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.in.ExecuteIngestUseCase;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
class IngestConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestConsumer.class);
  
  @Inject
  ExecuteIngestUseCase ingestUseCase;

  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  Uni<Void> onMessage(Message<IngestId> message) {
    try {
      var ingestId = message.getPayload();
      ingestUseCase.ingest(ingestId);
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      LOGGER.error("Ingest failed.", e);
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
}
