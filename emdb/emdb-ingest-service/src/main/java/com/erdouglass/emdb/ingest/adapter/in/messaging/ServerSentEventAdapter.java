package com.erdouglass.emdb.ingest.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.messaging.IngestEvent;

@ApplicationScoped
class ServerSentEventAdapter {
  private static final Logger LOGGER = Logger.getLogger(ServerSentEventAdapter.class);

  @Incoming("ingest-events-sse")
  void onMessage(IngestEvent event) {
    LOGGER.infof("SSE correlation id: %s, message id: %s, time: %s, status: %s", 
        event.correlationId().value(), event.messageId().value(), event.occurredAt(), event.eventType());
  }
}
