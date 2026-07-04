package com.erdouglass.emdb.ingest.core;

import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.LoggingDecorator;
import com.erdouglass.emdb.ingest.IngestEvent;
import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.IngestService;
import com.erdouglass.emdb.ingest.IngestSubmitted;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
class IngestProducer implements IngestService {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  
  @Inject
  @Channel("ingest-media-out")
  Emitter<IngestMedia> commandEmitter;
  
  @Inject
  Event<IngestEvent> eventEmitter;

  public UUID publish(@NotNull @Valid IngestMedia command) {
    var correlationId = GENERATOR.generate();
    var msg = String.format("Ingest of TMDB %s %d submitted.", command.type(), command.tmdbId());
    eventEmitter.fire(new IngestSubmitted(correlationId, msg, command.type()));
    commandEmitter.send(Message.of(command)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withCorrelationId(correlationId.toString())
            .withHeader(LoggingDecorator.EVENT_TYPE, command.getClass().getSimpleName())
            .withHeader(IngestMedia.START_TIME, Instant.now().toString()).build()));
    return correlationId;
  }  
}
