package com.erdouglass.emdb.common.service;

import java.util.concurrent.CompletableFuture;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.common.event.IngestStatusChanged;

import io.opentelemetry.context.Context;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class IngestStatusService {
  public static final String ROUTE_KEY = "ingest.status.changed";
  
  @Inject
  @Channel("ingest-status-out") 
  Emitter<IngestStatusChanged> emitter;
  
  public void send(@NotNull @Valid IngestStatusChanged event) {
    var ctx = Context.current();
    var ackFuture = new CompletableFuture<>();
    
    try {
      var message = Message.of(event)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
              .withRoutingKey(ROUTE_KEY)
              .build())
          .withAck(() -> {
              ackFuture.complete(null);
              return CompletableFuture.completedFuture(null);
          });
      emitter.send(message);
      ackFuture.join();
    } finally {
      ctx.makeCurrent();   
    }
  }
  
  public String causedBy(Throwable throwable) {
    Throwable rootCause = throwable;
    while (rootCause.getCause() != null && rootCause != rootCause.getCause()) {
      rootCause = rootCause.getCause();
    }
    return rootCause.getMessage(); 
  }

}
