package com.erdouglass.emdb.ingest.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.messaging.IngestEvent;

@ApplicationScoped
class ServerSentEventProducer {
  private static final Logger LOGGER = Logger.getLogger(ServerSentEventProducer.class);

  @Incoming("ingest-events-sse")
  void onMessage(IngestEvent event) {
    LOGGER.infof("SSE correlation id: %s, event id: %s, time: %s, status: %s", 
        event.correlationId().value(), event.id().value(), event.occurredAt(), event.eventType());
  }
}
