package com.erdouglass.emdb.scraper.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.messaging.IngestMediaMessage;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
class MediaConsumer {
  private static final Logger LOGGER = Logger.getLogger(MediaConsumer.class);

  @RunOnVirtualThread
  @Incoming("ingest-media")
  void onMessage(IngestMediaMessage message) {
    LOGGER.infof("Received: %s", message);
  }
}
