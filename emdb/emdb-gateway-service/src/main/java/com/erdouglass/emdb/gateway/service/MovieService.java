package com.erdouglass.emdb.gateway.service;

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
import com.erdouglass.emdb.common.message.JobMessage;
import com.erdouglass.emdb.common.message.JobMessage.JobSource;
import com.erdouglass.emdb.common.message.JobMessage.JobStatus;

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
  
  @Inject
  @Channel("job-log-out")
  Emitter<JobMessage> jobEmitter;
  
  public void cron() {
    var cronMessage = CronMessage.of(UUID.randomUUID());
    var message = Message.of(cronMessage)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withRoutingKey(CRON_KEY)
            .build());           
    cronEmitter.send(message);
    LOGGER.infof("Sent: %s", cronMessage);    
  }
  
  public UUID ingest(@NotNull @Positive Integer tmdbId) {
    var jobId = UUID.randomUUID();
    var jobMessage = JobMessage.builder()
        .id(jobId)
        .source(JobSource.GATEWAY)
        .status(JobStatus.SUBMITTED)
        .content(String.format("TMDB movie %d submitted for ingestion", tmdbId))
        .progress(0)
        .build();
    jobEmitter.send(Message.of(jobMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(Configuration.JOB_KEY)
        .build()));
    
    var ingestMessage = IngestMessage.of(jobId, tmdbId);
    ingestEmitter.send(Message.of(ingestMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(Configuration.INGEST_KEY)
        .build())); 
    LOGGER.infof("Sent: %s", ingestMessage);
    return jobId;
  }

}
