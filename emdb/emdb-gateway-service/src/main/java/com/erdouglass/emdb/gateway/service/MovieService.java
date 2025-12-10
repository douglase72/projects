package com.erdouglass.emdb.gateway.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.EventStatus;
import com.erdouglass.emdb.common.command.IngestCommand;

import io.opentelemetry.api.trace.Span;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  private static final String INGEST_KEY = "movie.ingest";
  
  @Inject
  @Channel("movie-ingest-out") 
  Emitter<IngestCommand> emitter;
  
  public String ingest(@NotNull @Positive Integer tmdbId) {
    var traceId = Span.current().getSpanContext().getTraceId();
    var cmd = IngestCommand.of(traceId, EventStatus.QUEUED, tmdbId);
    var message = Message.of(cmd)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withRoutingKey(INGEST_KEY)
            .build());
    emitter.send(message);
    LOGGER.infof("Sent: %s", cmd);
    return traceId;
  }

}
