package com.erdouglass.emdb.gateway.service;

import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.IngestMedia;

import io.opentelemetry.api.baggage.Baggage;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class IngestService {
  private static final Logger LOGGER = Logger.getLogger(IngestService.class);
  
  @Inject
  @Channel("ingest-media-out") 
  Emitter<IngestMedia> commandEmitter;
  
  public String ingest(IngestMedia command) {
    var jobId = UUID.randomUUID().toString();
    var baggage = Baggage.current().toBuilder()
        .put("job-id", jobId)
        .put("job-start-time", Instant.now().toString())
        .build();
    try (var _ = baggage.makeCurrent()) {
      commandEmitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(Configuration.MEDIA_KEY)
          .build()));
      LOGGER.infof("Ingest Job %s for TMDB %s %d submitted", jobId, command.type(), command.tmdbId());  
    }
    return jobId;
  }

}
