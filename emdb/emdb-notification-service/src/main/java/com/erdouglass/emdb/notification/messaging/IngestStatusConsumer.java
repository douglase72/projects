package com.erdouglass.emdb.notification.messaging;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.messaging.LoggingDecorator;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class IngestStatusConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestStatusConsumer.class);
  
  @RunOnVirtualThread
  @Incoming("status-events-in")
  public CompletionStage<Void> onMessage(Message<IngestStatusChanged> message) {
    var event = message.getPayload();
    
    try {
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
