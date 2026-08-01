package com.erdouglass.emdb.ingest.adapter.outbound.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.application.port.outbound.IngestProducer;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class RabbitMQIngestAdapter implements IngestProducer {
  
  @Inject
  @Channel("ingest-media-out")
  Emitter<IngestId> emitter;

  @Override
  public void publish(@NotNull IngestId id) {
    emitter.send(Message.of(id)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withCorrelationId(id.toString())
            .build()));
  }
}
