package com.erdouglass.emdb.media.consumer;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.eclipse.microprofile.faulttolerance.Retry;
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
import com.erdouglass.emdb.media.service.MovieService;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  @Channel("job-log-out")
  Emitter<JobMessage> jobEmitter;
  
  @Inject
  MovieService service;
  
  @Inject
  Validator validator;
  
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  @Retry(maxRetries = 3, abortOn = { ConstraintViolationException.class })
  public void onMessage(MovieCreateMessage message) {
    var jobId = message.id();
    var tmdbId = message.tmdbId();
    var latency = Duration.between(message.timestamp(), Instant.now()).toMillis();
    LOGGER.infof("Message: %s, latency: %d ms", message, latency);
    
    try {
      var violations = validator.validate(message);
      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(violations);
      }      
      var msg = String.format("Persistence started for TMDB movie %d", tmdbId);
      updateProgress(jobId, JobStatus.PROGRESS, msg, 72); 
      
      var movie = service.create(message);
      msg = String.format("Created EMDB movie %d", movie.id());
      updateProgress(jobId, JobStatus.PROGRESS, msg, 99);  
      
      msg = String.format("Ingest completed for TMDB movie %d", movie.tmdbId());
      updateProgress(jobId, JobStatus.COMPLETED, msg, 100);
    } catch (ConstraintViolationException e) {
      var msg = String.format("Failed to validate TMDB movie %d", tmdbId);
      updateProgress(jobId, JobStatus.FAILED, msg, 0);
      throw new RuntimeException(msg, e);
    } catch (Exception e) {
      var msg = String.format("Failed to persist TMDB movie %d", tmdbId);
      updateProgress(jobId, JobStatus.FAILED, msg, 0);
      throw new RuntimeException(msg, e);
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
  }

}
