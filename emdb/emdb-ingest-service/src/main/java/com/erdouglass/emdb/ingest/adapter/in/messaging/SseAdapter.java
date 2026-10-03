package com.erdouglass.emdb.ingest.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.messaging.IngestEventMessage;

@ApplicationScoped
class SseAdapter {
  private static final Logger LOGGER    = Logger.getLogger(SseAdapter.class);

  @Incoming("ingest-events-sse")
  void onMessage(IngestEventMessage message) {
    LOGGER.infof("Sending %s via SSE", message);
  }
}
