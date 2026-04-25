package com.erdouglass.emdb.common.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Publishes [IngestStatusChanged] events to the broker.
///
/// Sets the routing key and the correlation ID on every outbound message
/// so that downstream consumers can route, deduplicate, and correlate
/// events for a given ingest job.
@ApplicationScoped
public class IngestStatusProducer {
  public static final String ROUTING_KEY = "status.changed";

  @Inject
  @Channel("status-events-out")
  Emitter<IngestStatusChanged> emitter;
  
  /// Publishes a status event to the broker.
  ///
  /// The event's [IngestStatusChanged#id] is used as the message
  /// correlation ID so consumers can group all events for a single
  /// ingest job.
  ///
  /// @param event the event to publish
  public void send(@NotNull @Valid IngestStatusChanged event) {
    emitter.send(Message.of(event)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
              .withRoutingKey(ROUTING_KEY)
              .withCorrelationId(event.id().toString())
              .build()));  
  }
}
