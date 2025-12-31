package com.erdouglass.emdb.media.consumer;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.message.MovieCreateMessage;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.baggage.Baggage;
import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  MeterRegistry registry;
  
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  public void onMessage(MovieCreateMessage message) {
    var jobId = Baggage.current().getEntryValue("job-id"); 
    LOGGER.infof("Received: %s for TMDB movie %d", jobId, message.tmdbId());
    logIngestTime(jobId, message.tmdbId());
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

}
