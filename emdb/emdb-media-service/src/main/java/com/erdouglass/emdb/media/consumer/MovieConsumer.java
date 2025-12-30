package com.erdouglass.emdb.media.consumer;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.message.MovieCreateMessage;
import com.erdouglass.emdb.media.service.MovieService;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.baggage.Baggage;
import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  MeterRegistry registry;
  
  @Inject
  MovieService service;
  
  @Inject
  Validator validator;
  
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  public CompletionStage<Void> onMessage(Message<MovieCreateMessage> wrapper) {
    var message = wrapper.getPayload();
    var jobId = Baggage.current().getEntryValue("job-id"); 
    LOGGER.infof("Received: %s for TMDB movie %d", jobId, message.tmdbId());
    
    try {
      validate(message);
      long startTime = System.nanoTime();
      var movie = service.create(message);
      long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      LOGGER.infof("Created %s in %d ms", movie, et);
      logIngestTime(jobId, message.tmdbId());
      return wrapper.ack();
    } catch (ConstraintViolationException e) {
      var msg = String.format("Failed to validate TMDB movie %d", message.tmdbId());
      LOGGER.error(msg, e);
      return wrapper.nack(e);
    } catch (Exception e) {
      var msg = String.format("Failed to ingest TMDB movie %d", message.tmdbId());
      LOGGER.error(msg, e);
      return wrapper.nack(e);
    }
  }
  
  private void logIngestTime(String jobId, int tmdbId) {
    var start = Instant.parse(Baggage.current().getEntryValue("job-start-time"));
    var et = Duration.between(start, Instant.now());
    LOGGER.infof("Job %s for TMDB movie %d completed in %d ms", jobId, tmdbId, et.toMillis());
    Timer.builder("emdb.method.duration")
        .description("Measures the time to ingest a movie from TMDB")
        .tag("class", "MovieConsumer") 
        .tag("method", "Movie Ingest")
        .register(registry)
        .record(et);
  }
  
  private void validate(MovieCreateMessage message) {
    var violations = validator.validate(message);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    } 
  }

}
