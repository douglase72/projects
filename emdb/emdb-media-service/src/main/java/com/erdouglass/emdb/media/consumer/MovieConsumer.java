package com.erdouglass.emdb.media.consumer;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.message.JobMessage;
import com.erdouglass.emdb.common.message.JobMessage.JobSource;
import com.erdouglass.emdb.common.message.JobMessage.JobStatus;
import com.erdouglass.emdb.common.message.MovieCreateMessage;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  @Channel("job-log-out")
  Emitter<JobMessage> jobEmitter;
  
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  public void onMessage(MovieCreateMessage message) {
    var jobId = message.id();
    var tmdbId = message.tmdbId();
    var latency = Duration.between(message.timestamp(), Instant.now()).toMillis();
    LOGGER.infof("Message: %s, latency: %d ms", message, latency);
    
    try {
      var msg = String.format("Persistence started for TMDB movie %d", tmdbId);
      updateProgress(jobId, JobStatus.PROGRESS, msg, 72); 
      
      Thread.sleep(3000);
      msg = String.format("Persistence completed for TMDB movie %d", tmdbId);
      updateProgress(jobId, JobStatus.COMPLETED, msg, 100);      
    } catch (Exception e) {
      var msg = String.format("Failed to persist TMDB movie %d", tmdbId);
      updateProgress(jobId, JobStatus.FAILED, msg, 0);
      LOGGER.error(msg, e);
    } 
  }
  
  private void updateProgress(
      UUID id, JobStatus status, String message, Integer progress) {
    var jobMessage = JobMessage.builder()
        .id(id)
        .source(JobSource.MEDIA)
        .status(status)
        .content(message)
        .progress(progress)
        .build();
    jobEmitter.send(Message.of(jobMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(Configuration.JOB_KEY)
        .build()));
    LOGGER.infof("Sent: %s", jobMessage);
  }

}
