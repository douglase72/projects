package com.erdouglass.emdb.gateway.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.IngestMessage;

import io.opentelemetry.api.trace.Span;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  private static final String INGEST_KEY = "movie.ingest";
  
  @Inject
  @Channel("movie-ingest-out") 
  Emitter<IngestMessage> ingestEmitter;
  
  public String ingest(@NotNull @Positive Integer tmdbId) {
    var jobId = Span.current().getSpanContext().getTraceId();
    var ingestMessage = IngestMessage.of(jobId, tmdbId);
    ingestEmitter.send(Message.of(ingestMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(INGEST_KEY)
        .build())); 
    LOGGER.infof("Sent: %s", ingestMessage);
    return jobId;
  }

}
