package com.erdouglass.emdb.gateway.messaging;

import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.webservices.LoggingDecorator;
import com.fasterxml.uuid.Generators;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class IngestProducer {
  
  @Inject
  @Channel("ingest-media-out") 
  Emitter<IngestMedia> emitter;  

  public UUID ingest(IngestMedia command) {
    var correlationId = Generators.timeBasedEpochGenerator().generate();
    MDC.put(LoggingDecorator.CORRELATION_ID, correlationId.toString());
    
    try {
      emitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
              .withRoutingKey(Configuration.INGEST_KEY)
              .withCorrelationId(correlationId.toString())
              .withHeader(Configuration.JOB_START_TIME, Instant.now().toString())
              .withHeader("X-Event-Type", command.getClass().getSimpleName())
              .build()));
      return correlationId;
    } finally {
      MDC.remove(LoggingDecorator.CORRELATION_ID);
    }    
  }
}
