package com.erdouglass.emdb.gateway.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.common.service.IngestStatusService;

import io.opentelemetry.api.baggage.Baggage;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class IngestService {
  private static final Logger LOGGER = Logger.getLogger(IngestService.class);
  
  /// A BroadcastProcessor allows multiple subscribers (SSE clients) to share the same stream.
  private final BroadcastProcessor<IngestStatusChanged> broadcaster = BroadcastProcessor.create();
  
  @Inject
  @ConfigProperty(name = "emdb.heartbeat.interval")
  Integer heartbeatInterval;  
  
  @Inject
  @Channel("ingest-media-out") 
  Emitter<IngestMedia> emitter;
  
  @Inject
  IngestStatusService statusService;
  
  public String ingest(IngestMedia command) {
    var jobId = UUID.randomUUID().toString();
    var baggage = Baggage.current().toBuilder()
        .put("job-id", jobId)
        .put("job-start-time", Instant.now().toString())
        .build();
    try (var _ = baggage.makeCurrent()) {
      emitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(Configuration.MEDIA_KEY)
          .build()));
      LOGGER.infof("Ingest Job %s for TMDB %s %d submitted", jobId, command.type(), command.tmdbId());
      statusService.send(IngestStatusChanged.builder()
          .id(jobId)
          .status(IngestStatus.SUBMITTED)
          .tmdbId(command.tmdbId())
          .source(IngestSource.GATEWAY)
          .type(command.type())
          .build());
    }
    return jobId;
  }
  
  @Incoming("ingest-status-in")
  public void onMessage(IngestStatusChanged event) {
    LOGGER.infof("Received: %s", event);
    broadcaster.onNext(event);
  }
  
  /// Provides the stream of real-time events.
  ///
  /// @return A Multi stream that emits events as they arrive.  
  public Multi<IngestStatusChanged> stream() {
    Multi<IngestStatusChanged> heartbeat = Multi.createFrom()
        .ticks().every(Duration.ofSeconds(heartbeatInterval))
        .map(_ -> IngestStatusChanged.builder()
            .id(UUID.randomUUID().toString())
            .status(IngestStatus.HEARTBEAT)
            .source(IngestSource.GATEWAY)
            .build());  
    return Multi.createBy().merging().streams(broadcaster, heartbeat);
  }

}
