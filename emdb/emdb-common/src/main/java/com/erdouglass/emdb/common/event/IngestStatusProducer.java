package com.erdouglass.emdb.common.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class IngestStatusProducer {
  public static final String ROUTING_KEY = "status.changed";

  @Inject
  @Channel("status-events-out")
  Emitter<IngestStatusChanged> emitter;
  
  public void send(@NotNull @Valid IngestStatusChanged event) {
    emitter.send(Message.of(event)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
              .withRoutingKey(ROUTING_KEY)
              .withCorrelationId(event.id().toString())
              .build()));  
  }
}
