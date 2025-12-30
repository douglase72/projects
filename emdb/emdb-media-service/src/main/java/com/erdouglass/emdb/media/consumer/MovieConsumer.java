package com.erdouglass.emdb.media.consumer;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.message.MovieCreateMessage;

import io.opentelemetry.api.baggage.Baggage;
import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  public void onMessage(MovieCreateMessage message) {
    var jobId = Baggage.current().getEntryValue("job-id"); 
    LOGGER.infof("Received: %s for TMDB movie %d", jobId, message.tmdbId());
    
    var start = Instant.parse(Baggage.current().getEntryValue("job-start-time"));
    var et = Duration.between(start, Instant.now());
    LOGGER.infof("Job %s for TMDB movie %d completed in %d ms", jobId, message.tmdbId(), et.toMillis());
  }

}
