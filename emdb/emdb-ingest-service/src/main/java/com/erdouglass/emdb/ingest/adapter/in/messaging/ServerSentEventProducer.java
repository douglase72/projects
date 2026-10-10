package com.erdouglass.emdb.ingest.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.ingest.messaging.IngestEvent;

@ApplicationScoped
class ServerSentEventProducer {

  @Incoming("ingest-events-sse")
  void onMessage(IngestEvent event) {
    
  }
}
