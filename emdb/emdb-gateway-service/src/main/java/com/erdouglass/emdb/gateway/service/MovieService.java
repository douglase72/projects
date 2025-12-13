package com.erdouglass.emdb.gateway.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.common.command.AuditMessage;
import com.erdouglass.emdb.common.command.AuditMetadata.EventSource;
import com.erdouglass.emdb.common.command.AuditMetadata.EventType;

import io.opentelemetry.api.trace.Span;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieService {
  private static final String INGEST_KEY = "movie.ingest";
  
  @Inject
  @Channel("movie-ingest-out") 
  Emitter<AuditMessage> emitter;
  
  public String ingest(@NotNull @Positive Integer tmdbId) {
    var traceId = Span.current().getSpanContext().getTraceId();
    var msg = String.format("TMDB movie %d submitted for ingestion", tmdbId);
    var cmd = AuditMessage.of(traceId, EventSource.GATEWAY, EventType.SUBMITTED, msg, 0, tmdbId);
    var message = Message.of(cmd)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withRoutingKey(INGEST_KEY)
            .build());
    emitter.send(message);
    return traceId;
  }

}
