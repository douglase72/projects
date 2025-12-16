package com.erdouglass.emdb.gateway.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.message.AuditMessage;
import com.erdouglass.emdb.common.message.AuditMessage.MessageSource;
import com.erdouglass.emdb.common.message.AuditMessage.MessageType;
import com.erdouglass.emdb.common.message.IngestMessage;

import io.opentelemetry.api.trace.Span;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  private static final String INGEST_KEY = "movie.ingest";
  
  @Inject
  @Channel("audit-log-out")
  Emitter<AuditMessage> auditEmitter;
  
  @Inject
  @Channel("movie-ingest-out") 
  Emitter<IngestMessage> ingestEmitter;
  
  public String ingest(@NotNull @Positive Integer tmdbId) {
    var jobId = Span.current().getSpanContext().getTraceId();
    var msg = String.format("TMDB movie %d submitted for ingestion", tmdbId);
    var auditMessage = AuditMessage.of(jobId, MessageSource.GATEWAY, MessageType.SUBMITTED, msg, 0);
    auditEmitter.send(Message.of(auditMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(Configuration.AUDIT_KEY)
        .build()));
    LOGGER.infof("Sent: %s", auditMessage);
    
    var ingestMessage = IngestMessage.of(jobId, tmdbId);
    ingestEmitter.send(Message.of(ingestMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(INGEST_KEY)
        .build())); 
    LOGGER.infof("Sent: %s", ingestMessage);
    return jobId;
  }

}
