package com.erdouglass.emdb.scheduler.messaging;

import java.time.Instant;

import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.api.MediaType;
import com.erdouglass.emdb.common.api.command.IngestMedia;
import com.erdouglass.emdb.common.api.messaging.IngestSource;
import com.erdouglass.emdb.common.api.messaging.IngestStatus;
import com.erdouglass.emdb.common.api.messaging.IngestStatusChanged;
import com.erdouglass.emdb.common.api.messaging.IngestStatusEmitter;
import com.erdouglass.messaging.LoggingDecorator;
import com.fasterxml.uuid.Generators;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

public abstract class TmdbScheduler {
  
  @Inject
  @Channel("ingest-media-out") 
  Emitter<IngestMedia> emitter;
  
  @Inject
  IngestStatusEmitter statusEmitter;
  
  public abstract void execute();
  
  public abstract MediaType type();
  
  protected void ingest(IngestMedia command) {
    var correlationId = Generators.timeBasedEpochGenerator().generate();
    MDC.put(LoggingDecorator.CORRELATION_ID, correlationId.toString());
    
    try {
      emitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
              .withRoutingKey(IngestMedia.INGEST_KEY)
              .withCorrelationId(correlationId.toString())
              .withHeader(IngestMedia.START_TIME, Instant.now().toString())
              .withHeader("X-Event-Type", command.getClass().getSimpleName())
              .build()));
      statusEmitter.send(IngestStatusChanged.builder()
          .id(correlationId)
          .tmdbId(command.tmdbId())
          .status(IngestStatus.SUBMITTED)
          .source(IngestSource.SCHEDULER)
          .type(command.type())
          .message(String.format("Ingest Job for TMDB %s %d submitted", command.type(), command.tmdbId()))
          .build());
    } finally {
      MDC.remove(LoggingDecorator.CORRELATION_ID);
    }
  }
}
