package com.erdouglass.emdb.media.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.common.service.IngestStatusService;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.baggage.Baggage;
import io.smallrye.common.annotation.RunOnVirtualThread;

/// This class consumes the {@link IngestMedia} command. It takes one command at a time 
/// from the ingest-media queue and extracts, transforms, and loads the relevant data 
/// from TMDB. There can be only a single instance of this consumer to ensure the TMDB 
/// rate limit is respected.
@ApplicationScoped
public class IngestService {
  private static final Logger LOGGER = Logger.getLogger(IngestService.class);
  
  @Inject
  MovieService movieService;
  
  @Inject
  PersonService personService;
  
  @Inject
  SeriesService seriesService;
  
  @Inject
  IngestStatusService statusService;  
  
  @Inject
  MeterRegistry registry;  
  
  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public CompletionStage<Void> onMessage(Message<IngestMedia> wrapper) {
    var command = wrapper.getPayload();
    var jobId = UUID.fromString(Baggage.current().getEntryValue("job-id"));
    logQueueDuration(jobId, command);
    
    try {
      var et = switch (command.type()) {
        case MOVIE -> movieService.ingest(command.tmdbId(), jobId);
        case SERIES -> seriesService.ingest(command.tmdbId(), jobId);
        case PERSON -> personService.ingest(command.tmdbId(), jobId);
      };
      Timer.builder("emdb.ingest.duration")
        .description("Measures the time to ingest a movie from TMDB")
        .tag("media", command.type().toString())
        .register(registry)
        .record(et);
      return wrapper.ack();
    } catch (Exception e) {
      var msg = String.format("Failed to ingest TMDB %s %d", command.type(), command.tmdbId());
      LOGGER.error(msg, e);
      statusService.send(IngestStatusChanged.builder()
          .id(jobId)
          .status(IngestStatus.FAILED)
          .tmdbId(command.tmdbId())
          .source(IngestSource.MEDIA)
          .type(command.type())
          .message(statusService.causedBy(e))
          .build());
      return wrapper.nack(e);
    }
  }
  
  private void logQueueDuration(UUID jobId, IngestMedia command) {
    var start = Instant.parse(Baggage.current().getEntryValue("job-start-time"));
    var et = Duration.between(start, Instant.now());
    var msg = String.format("Ingest Job for TMDB %s %d sat in the ingest-media queue for %d ms", 
        command.type(), command.tmdbId(), et.toMillis());
    LOGGER.info(msg);
    statusService.send(IngestStatusChanged.builder()
        .id(jobId)
        .status(IngestStatus.STARTED)
        .tmdbId(command.tmdbId())
        .source(IngestSource.MEDIA)
        .type(command.type())
        .message(msg)
        .build());
  }

}
