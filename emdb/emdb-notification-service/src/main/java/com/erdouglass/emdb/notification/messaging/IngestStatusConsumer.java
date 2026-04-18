package com.erdouglass.emdb.notification.messaging;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.notification.mapper.IngestMapper;
import com.erdouglass.emdb.notification.repository.IngestRepository;
import com.erdouglass.messaging.LoggingDecorator;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class IngestStatusConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestStatusConsumer.class);
  
  @Inject
  IngestMapper mapper;
  
  @Inject
  IngestRepository repository;
  
  @Transactional
  @RunOnVirtualThread
  @Incoming("status-events-in")
  public CompletionStage<Void> onMessage(Message<IngestStatusChanged> message) {
    var event = message.getPayload();
    
    try {
      var ingest = repository.findById(event.id())
          .map(e -> { mapper.merge(event, e); return repository.update(e); })
          .orElseGet(() -> repository.insert(mapper.toIngest(event)));
      var statusEvent = mapper.toStatusEvent(event);
      statusEvent.setIngest(ingest);
      repository.insertStatus(statusEvent);
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
