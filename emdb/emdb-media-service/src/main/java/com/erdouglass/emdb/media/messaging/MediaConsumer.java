package com.erdouglass.emdb.media.messaging;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.messaging.LoggingDecorator;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

/// Consumes [IngestMedia] commands from the RabbitMQ ingest-media queue.
///
/// Each message triggers an extraction from TMDB followed by persistence
/// to the database. The queue is configured with max-outstanding-messages
/// set to one to avoid overwhelming the TMDB API with concurrent requests.
///
/// Messages are acknowledged on success and negatively acknowledged on
/// failure, allowing RabbitMQ to handle redelivery.
@ApplicationScoped
public class MediaConsumer {
  private static final Logger LOGGER = Logger.getLogger(MediaConsumer.class);
  
  /// Processes a single [IngestMedia] message from the ingest-media queue.
  ///
  /// Extracts the media data from TMDB based on the command's media type
  /// and TMDB ID, then persists the result to the database. Logs both the
  /// time the message spent waiting in the queue and the total job duration.
  ///
  /// @param message the inbound message containing the [IngestMedia] payload
  /// @return a completion stage that acknowledges or negatively acknowledges the message
  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public CompletionStage<Void> onMessage(Message<IngestMedia> message) {
    var command = message.getPayload();
    
    try {
      logQueueDuration(message); 
      var start = Instant.now(); 
      
      // Simulate extracting the media data from  TMDB.
      TimeUnit.SECONDS.sleep(1);
      logJobDuration(command.tmdbId(), command.type(), start);
      return message.ack();
    } catch (Exception e) {
      var msg = String.format("Failed to ingest TMDB %s %d", command.type(), command.tmdbId());
      LOGGER.error(msg, e);      
      return message.nack(e);      
    } finally {
      MDC.remove(LoggingDecorator.CORRELATION_ID);
    }
  }
  
  private void logJobDuration(int tmdbId, MediaType type, Instant start) {
    var et = Duration.between(start, Instant.now());
    var msg = String.format("Ingest job for TMDB %s %d completed in %d ms", type, tmdbId, et.toMillis());
    LOGGER.info(msg);
  }
  
  private void logQueueDuration(Message<IngestMedia> message) {
    var command = message.getPayload();
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
        .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
    var start = Instant.parse(metadata.getHeaders().get(Configuration.JOB_START_TIME).toString());
    var et = Duration.between(start, Instant.now());
    var msg = String.format("Ingest job for TMDB %s %d sat in the ingest-media queue for %d ms", 
        command.type(), command.tmdbId(), et.toMillis());
    LOGGER.info(msg);
  }
}
