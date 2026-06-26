package com.erdouglass.emdb.ingest.core;

import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.LoggingDecorator;
import com.erdouglass.emdb.media.IngestMedia;
import com.erdouglass.emdb.media.IngestService;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
class IngestProducer implements IngestService {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  
  @Inject
  @Channel("ingest-media-out")
  Emitter<IngestMedia> emitter;

  public UUID publish(@NotNull @Valid IngestMedia command) {
    var correlationId = GENERATOR.generate();
    emitter.send(Message.of(command)
        .addMetadata(OutgoingRabbitMQMetadata.builder().withCorrelationId(correlationId.toString())
            .withHeader(LoggingDecorator.EVENT_TYPE, command.getClass().getSimpleName())
            .withHeader(IngestMedia.START_TIME, Instant.now().toString()).build()));
    return correlationId;
  }  
}
