package com.erdouglass.emdb.ingest.adapter.inbound.messaging;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class IngestConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestConsumer.class);

  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public Uni<Void> onMessage(Message<IngestId> message) {
    var id = message.getPayload();
    
    try {
      LOGGER.infof("id: %s", id);
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      LOGGER.errorf("Ingest failed: %s", id);
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
}
