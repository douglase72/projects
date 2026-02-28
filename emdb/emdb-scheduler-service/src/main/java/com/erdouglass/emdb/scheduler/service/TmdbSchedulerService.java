package com.erdouglass.emdb.scheduler.service;

import java.time.Instant;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.ExecuteScheduler;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.comand.IngestMedia.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.common.service.IngestStatusService;
import com.fasterxml.uuid.Generators;

import io.opentelemetry.api.baggage.Baggage;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Periodically ingests movie data from TMDB.
/// 
/// Cron Examples:
/// - @Scheduled(cron = "0 0 0 * * ?") - Run every day at midnight UTC (17:00:00 MST)
/// - @Scheduled(cron = "0 0 8 * * ?") - Run every day at 08:00:00 UTC (01:00:00 MST)
/// - @Scheduled(cron = "0 10 0 * * ?") - Run every day at 00:10:00 UTC
/// - @Scheduled(every = "10m", delay = 30, delayUnit = TimeUnit.SECONDS) - Run every 10 minutes with a 30s delay
@ApplicationScoped
public class TmdbSchedulerService {
  private static final Logger LOGGER = Logger.getLogger(TmdbSchedulerService.class);
  
  @Inject
  @Channel("ingest-media-out") 
  Emitter<IngestMedia> emitter;
  
  @Inject
  IngestStatusService statusService;
  
  @Scheduled(cron = "{emdb.movie.scheduler}")  
  public void ingestMovies() {
    var changedMovies = List.of(335984, 818);
    for (var tmdbId : changedMovies) {      
      ingest(tmdbId, MediaType.MOVIE);
    }    
  }
  
  @Scheduled(cron = "{emdb.series.scheduler}")
  public void ingestSeries() {
    var changedSeries = List.of(113959, 4614, 456);
    for (var tmdbId : changedSeries) {      
      ingest(tmdbId, MediaType.SERIES);
    }
  }
  
  @Scheduled(cron = "{emdb.person.scheduler}")
  public void ingestPeople() {
    var changedPeople = List.of(13918, 12073);
    for (var tmdbId : changedPeople) {      
      ingest(tmdbId, MediaType.PERSON);
    }    
  }
  
  @RunOnVirtualThread
  @Incoming("execute-scheduler-in")
  public void execute(ExecuteScheduler command) {
    LOGGER.infof("Received: %s", command);
    switch (command.type()) {
      case MOVIES -> ingestMovies();
      case SERIES -> ingestSeries();
      case PEOPLE -> ingestPeople();
    }
  } 
  
  private void ingest(int tmdbId, MediaType type) {
    var jobId = Generators.timeBasedEpochGenerator().generate();
    var baggage = Baggage.current().toBuilder()
        .put("job-id", jobId.toString())
        .put("job-start-time", Instant.now().toString())
        .build();
    var command = IngestMedia.of(tmdbId, type, IngestSource.SCHEDULER);
    try (var _ = baggage.makeCurrent()) {
      emitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(Configuration.MEDIA_KEY)
          .build()));
      var msg = String.format("Ingest Job for TMDB %s %d submitted", command.type(), command.tmdbId());
      statusService.send(IngestStatusChanged.builder()
          .id(jobId)
          .status(IngestStatus.SUBMITTED)
          .tmdbId(command.tmdbId())
          .source(IngestStatusChanged.IngestSource.SCHEDULER)
          .type(command.type())
          .message(msg)
          .build());
    }
  }

}
