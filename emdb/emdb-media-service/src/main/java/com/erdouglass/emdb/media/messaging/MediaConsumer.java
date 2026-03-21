package com.erdouglass.emdb.media.messaging;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.webservices.LoggingDecorator;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

@ApplicationScoped
public class MediaConsumer {
  private static final Logger LOGGER = Logger.getLogger(MediaConsumer.class);
  
  @Inject
  MovieConsumer movieConsumer;
  
  @Inject
  SeriesConsumer seriesConsumer;
  
  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public CompletionStage<Void> onMessage(Message<IngestMedia> message) {
    var command = message.getPayload();
    
    try {
      var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
          .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
      logQueueDuration(metadata, command);
      var start = Instant.now();      
      switch (command.type()) {
        case MOVIE -> movieConsumer.ingest(command.tmdbId());
        case SERIES -> seriesConsumer.ingest(command.tmdbId());
        default -> throw new IllegalArgumentException("Invalid command type: " + command.type());
      }
      logJobDuration(command.tmdbId(), command.type(), Duration.between(start, Instant.now()));
      return message.ack();
    } catch (Exception e) {
      var msg = String.format("Failed to ingest TMDB %s %d", command.type(), command.tmdbId());
      LOGGER.error(msg, e);      
      return message.nack(e);
    } finally {
      MDC.remove(LoggingDecorator.CORRELATION_ID);
    }
  }
  
  private void logJobDuration(int tmdbId, MediaType type, Duration duration) {
    var msg = String.format("Ingest job for TMDB %s %d completed in %d ms", type, tmdbId, duration.toMillis());
    LOGGER.info(msg);
  }  
  
  private void logQueueDuration(IncomingRabbitMQMetadata metadata, IngestMedia command) {
    var start = Instant.parse(metadata.getHeaders().get(Configuration.JOB_START_TIME).toString());
    var et = Duration.between(start, Instant.now());
    var msg = String.format("Ingest job for TMDB %s %d sat in the ingest-media queue for %d ms", 
        command.type(), command.tmdbId(), et.toMillis());
    LOGGER.info(msg);
  }  

}
