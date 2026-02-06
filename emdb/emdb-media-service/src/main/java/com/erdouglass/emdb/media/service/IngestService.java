package com.erdouglass.emdb.media.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.IngestMedia;

import io.opentelemetry.api.baggage.Baggage;
import io.smallrye.reactive.messaging.annotations.Blocking;

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
  
  @Blocking
  @Incoming("ingest-media-in")
  public CompletionStage<Void> onMessage(Message<IngestMedia> wrapper) {
    var command = wrapper.getPayload();
    var jobId = Baggage.current().getEntryValue("job-id");
    logQueueDuration(jobId, command);
    LOGGER.infof("Ingest Job %s for TMDB %s %d started", jobId, command.type(), command.tmdbId());  
    
    try {
      var start = Instant.parse(Baggage.current().getEntryValue("job-start-time"));
      switch (command.type()) {
        case MOVIE -> movieService.ingest(command.tmdbId(), jobId);
        case SERIES -> seriesService.ingest(command.tmdbId(), jobId);
        case PERSON -> personService.ingest(command.tmdbId(), jobId);
      }
      var et = Duration.between(start, Instant.now());
      LOGGER.infof("Ingest Job %s for TMDB %s %d completed in %d ms", 
          jobId, command.type(), command.tmdbId(), et.toMillis());      
      return wrapper.ack();
    } catch (Exception e) {
      var msg = String.format("Failed to ingest TMDB media %d", command.tmdbId());
      LOGGER.error(msg, e);
      return wrapper.nack(e);
    }
  }
  
  private void logQueueDuration(String jobId, IngestMedia command) {
    var start = Instant.parse(Baggage.current().getEntryValue("job-start-time"));
    var et = Duration.between(start, Instant.now());
    LOGGER.infof("Ingest Job %s for TMDB %s %d sat in the ingest-media queue for %d ms", 
        jobId, command.type(), command.tmdbId(), et.toMillis());
  }

}
