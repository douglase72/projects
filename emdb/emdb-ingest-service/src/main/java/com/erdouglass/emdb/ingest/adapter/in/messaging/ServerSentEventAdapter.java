package com.erdouglass.emdb.ingest.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.ingest.messaging.IngestEventMessage;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

@ApplicationScoped
class ServerSentEventAdapter {
  private final BroadcastProcessor<IngestEventMessage> processor = BroadcastProcessor.create();

  @Incoming("ingest-events-sse")
  void onMessage(IngestEventMessage event) {
    processor.onNext(event);
  }
  
  Multi<IngestEventMessage> stream() {
    return processor;
  }
}
