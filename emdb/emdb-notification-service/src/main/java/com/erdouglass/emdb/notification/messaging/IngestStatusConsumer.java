package com.erdouglass.emdb.notification.messaging;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.api.messaging.IngestStatusChanged;
import com.erdouglass.emdb.notification.entity.Ingest;
import com.erdouglass.emdb.notification.entity.IngestStatusChange;
import com.erdouglass.emdb.notification.mapper.IngestMapper;
import com.erdouglass.emdb.notification.service.IngestService;
import com.erdouglass.messaging.LoggingDecorator;

import io.smallrye.common.annotation.RunOnVirtualThread;

/// Persists [IngestStatusChanged] events as they arrive from the broker.
///
/// Each event upserts the [Ingest] projection and inserts a corresponding
/// [IngestStatusChange] row. Failures are negatively acknowledged so the
/// broker may redeliver. Runs on a virtual thread to keep blocking JPA
/// work off the event loop.
@ApplicationScoped
public class IngestStatusConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestStatusConsumer.class);
  
  @Inject
  IngestMapper mapper;
  
  @Inject
  IngestService service;
  
  /// Handles one inbound status event.
  ///
  /// @param message the inbound message wrapping an [IngestStatusChanged]
  /// @return a completion stage that acks on success, nacks on failure
  @RunOnVirtualThread
  @Incoming("status-events-in")
  public CompletionStage<Void> onMessage(Message<IngestStatusChanged> message) {
    var event = message.getPayload();
    
    try {
      var ingest = mapper.toIngest(event);
      service.save(ingest);
      return message.ack();
    } catch (Exception e) {
      var msg = String.format("Failed to persist event %s", event);
      LOGGER.error(msg, e);      
      return message.nack(e);      
    } finally {
      MDC.remove(LoggingDecorator.CORRELATION_ID);
    }
  }
}
