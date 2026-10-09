package com.erdouglass.emdb.scraper.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.messaging.IngestCommand;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class IngestCommandConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestCommandConsumer.class);
  
  @Blocking
  @Incoming("ingest-commands")
  void onMessage(IngestCommand command) {
    LOGGER.infof("command: %s", command);
  }
}
