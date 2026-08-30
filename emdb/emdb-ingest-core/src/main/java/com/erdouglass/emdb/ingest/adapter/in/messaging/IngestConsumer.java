package com.erdouglass.emdb.ingest.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.application.port.in.ExecuteIngestUseCase;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
class IngestConsumer {

  @Inject
  ExecuteIngestUseCase ingestUseCase;
  
  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  Uni<Void> onMessage(Message<IngestId> message) {
    try {
      ingestUseCase.execute(message.getPayload());
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
}
