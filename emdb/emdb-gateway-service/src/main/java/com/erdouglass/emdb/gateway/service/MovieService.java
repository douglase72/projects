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
import com.erdouglass.emdb.common.command.IngestMessage;

import io.opentelemetry.api.trace.Span;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieService {
  private static final String INGEST_KEY = "movie.ingest";
  
  @Inject
  @Channel("audit-trail-out")
  Emitter<AuditMessage> auditEmitter;
  
  @Inject
  @Channel("movie-ingest-out") 
  Emitter<IngestMessage> ingestEmitter;
  
  public String ingest(@NotNull @Positive Integer tmdbId) {
    var traceId = Span.current().getSpanContext().getTraceId();
    var ingestMessage = IngestMessage.of(tmdbId);
    ingestEmitter.send(Message.of(ingestMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(INGEST_KEY)
        .build()));
    var msg = String.format("TMDB movie %d submitted for ingestion", tmdbId);
    updateProgress(traceId, EventType.SUBMITTED, msg, 0, tmdbId);
    return traceId;
  }
  
  private void updateProgress(
      String traceId, EventType type, String message, Integer complete, Integer tmdbId) {
    var updateMessage = AuditMessage.of(traceId, EventSource.GATEWAY, type, message, complete, tmdbId);
    auditEmitter.send(updateMessage);
  }

}
