package com.erdouglass.emdb.scheduler.service;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.message.CronMessage;
import com.erdouglass.emdb.common.message.IngestMessage;
import com.erdouglass.emdb.common.message.JobMessage;
import com.erdouglass.emdb.common.message.JobMessage.JobSource;
import com.erdouglass.emdb.common.message.JobMessage.JobStatus;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class TmdbMovieScheduler {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScheduler.class);
  
  @Inject
  @Channel("job-log-out")
  Emitter<JobMessage> jobEmitter;
  
  @Inject
  @Channel("movie-ingest-out") 
  Emitter<IngestMessage> ingestEmitter;
  
  /// Periodically fetches and synchronizes movie data from TMDB.
  /// 
  /// Cron Examples:
  /// - @Scheduled(cron = "0 0 0 * * ?") - Run every day at midnight UTC (17:00:00 MST)
  /// - @Scheduled(cron = "0 0 8 * * ?") - Run every day at 08:00:00 UTC (01:00:00 MST)
  /// - @Scheduled(cron = "0 10 0 * * ?") - Run every day at 00:10:00 UTC
  @Scheduled(cron = "0 0 8 * * ?")
  public void cron() {
    var tmdbIds = List.of(816, 818);
    for (var tmdbId : tmdbIds) {
      var jobId = UUID.randomUUID();
      sendJobMessage(jobId, tmdbId);
      sendIngestMessage(jobId, tmdbId);
    }   
  }
  
  private void sendIngestMessage(UUID jobId, int tmdbId) {
    var ingestMessage = IngestMessage.of(jobId, tmdbId);
    ingestEmitter.send(Message.of(ingestMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(Configuration.INGEST_KEY)
        .build())); 
    LOGGER.infof("Sent: %s", ingestMessage);
  }
  
  private void sendJobMessage(UUID jobId, int tmdbId) {
    var msg = String.format("TMDB movie %d submitted for ingestion", tmdbId);
    var jobMessage = JobMessage.builder()
        .id(jobId)
        .source(JobSource.SCHEDULER)
        .status(JobStatus.SUBMITTED)
        .content(msg)
        .progress(0)
        .build();
    jobEmitter.send(Message.of(jobMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(Configuration.JOB_KEY)
        .build()));
    LOGGER.infof("Sent: %s", jobMessage);
  }
  
  @Incoming("movie-cron-in")
  public void executeNow(CronMessage message) {
    var latency = Duration.between(message.timestamp(), Instant.now()).toMillis();
    LOGGER.infof("Message: %s, latency: %d ms", message, latency);
    cron();
  }

}
