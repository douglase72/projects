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
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.common.service.IngestStatusService;
import com.erdouglass.emdb.gateway.client.JobClient;
import com.fasterxml.uuid.Generators;

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
  @RestClient
  JobClient client;  
  
  @Inject
  @Channel("ingest-media-out") 
  Emitter<IngestMedia> emitter;
  
  @Inject
  IngestStatusService statusService;
  
  public UUID ingest(IngestMedia command) {
    var jobId = Generators.timeBasedEpochGenerator().generate();
    var startTime = Instant.now().toString();
    
    emitter.send(Message.of(command)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withRoutingKey(Configuration.MEDIA_KEY)
            .withHeader(Configuration.JOB_ID, jobId.toString())
            .withHeader(Configuration.JOB_START_TIME, startTime)
            .build()));
    LOGGER.debugf("Sent: %s", command);
      
    var msg = String.format("Ingest Job for TMDB %s %d submitted", command.type(), command.tmdbId());
    LOGGER.info(msg);
    statusService.send(IngestStatusChanged.builder()
        .id(jobId)
        .status(IngestStatus.SUBMITTED)
        .tmdbId(command.tmdbId())
        .source(IngestSource.GATEWAY)
        .type(command.type())
        .message(msg)
        .build());
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
  public Multi<Object> stream() {
    var history = client.findAll()
        .onItem().transformToMulti(list -> Multi.createFrom().iterable(list));
    var heartbeat = Multi.createFrom()
        .ticks().every(Duration.ofSeconds(heartbeatInterval))
        .map(_ -> IngestStatusChanged.builder()
            .status(IngestStatus.HEARTBEAT)
            .source(IngestSource.GATEWAY)
            .build());
    var live = Multi.createBy()
        .merging()
        .streams(broadcaster.onOverflow().buffer(256), heartbeat)
        .onItem().castTo(Object.class);
    return Multi.createBy().merging().streams(history, live);
  }

}
