package com.erdouglass.emdb.gateway.service;

import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.message.CronMessage;
import com.erdouglass.emdb.common.message.IngestMessage;

import io.opentelemetry.api.baggage.Baggage;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  private static final String CRON_KEY = "movie.cron";
  
  @Inject
  @Channel("movie-cron-out")
  Emitter<CronMessage> cronEmitter;
  
  @Inject
  @Channel("movie-ingest-out") 
  Emitter<IngestMessage> ingestEmitter;
  
  public void cron() {
    var message = Message.of(new CronMessage())
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withRoutingKey(CRON_KEY)
            .build());
    cronEmitter.send(message);
  }
  
  public UUID ingest(@NotNull @Positive Integer tmdbId) {
    var jobId = UUID.randomUUID();
    var baggage = Baggage.current().toBuilder()
        .put("job-id", jobId.toString())
        .put("job-start-time", Instant.now().toString())
        .build();
    try (var _ = baggage.makeCurrent()) {
      var message = IngestMessage.of(tmdbId);
      ingestEmitter.send(Message.of(message)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(Configuration.INGEST_KEY)
          .build())); 
      LOGGER.infof("Sent: %s, %s", jobId, message);
    }
    return jobId;
  }

}
