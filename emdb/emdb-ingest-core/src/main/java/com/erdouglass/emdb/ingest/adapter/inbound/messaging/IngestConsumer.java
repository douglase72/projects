package com.erdouglass.emdb.ingest.adapter.inbound.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.application.port.inbound.IngestMovieUseCase;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class IngestConsumer {
  
  @Inject
  IngestMovieUseCase ingestMovieUseCase;

  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public Uni<Void> onMessage(Message<IngestId> message) {
    
    try {
      ingestMovieUseCase.ingest(message.getPayload());
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
}
