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
      updateProgress(jobId, tmdbId, JobStatus.PROGRESS, "Persistence started for TMDB movie", 72); 
      
      service.create(message);
      updateProgress(jobId, tmdbId, JobStatus.PROGRESS, "Created EMDB movie", 99);  
      
      updateProgress(jobId, tmdbId, JobStatus.COMPLETED, "TMDB movie ingest completed", 100);
    } catch (ConstraintViolationException e) {
      var msg = "Failed to validate TMDB movie";
      updateProgress(jobId, tmdbId, JobStatus.FAILED, msg, 0);
      throw new RuntimeException(msg, e);
    } catch (Exception e) {
      var msg = "Failed to persist TMDB movie";
      updateProgress(jobId, tmdbId, JobStatus.FAILED, msg, 0);
      throw new RuntimeException(msg, e);
    } 
  }
  
  private void updateProgress(
      UUID id, int tmdbId, JobStatus status, String message, int progress) {
    var jobMessage = JobMessage.builder()
        .id(id)
        .tmdbId(tmdbId)
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
