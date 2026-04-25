package com.erdouglass.emdb.media.messaging;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.event.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatus;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusProducer;
import com.erdouglass.emdb.media.annotation.MessageMetadata;
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
  
  @Inject
  MovieConsumer movieConsumer;
  
  @Inject
  PersonConsumer personConsumer;
  
  @Inject
  SeriesConsumer seriesConsumer;
  
  @Inject
  IngestStatusProducer producer;
  
  /// Processes a single [IngestMedia] message from the ingest-media queue.
  ///
  /// Extracts the media data from TMDB based on the command's media type
  /// and TMDB ID, then persists the result to the database. Logs both the
  /// time the message spent waiting in the queue and the total job duration.
  ///
  /// @param message the inbound message containing the [IngestMedia] payload
  /// @return a completion stage that acknowledges or negatively acknowledges the message
  @RunOnVirtualThread
  @ActivateRequestContext
  @Incoming("ingest-media-in")
  public CompletionStage<Void> onMessage(Message<IngestMedia> message) {
    var command = message.getPayload();
    
    try {
      sendStartedStatus(message);
      switch (command.type()) {
        case MOVIE -> movieConsumer.ingest(message);
        case PERSON -> personConsumer.ingest(message);
        case SERIES -> seriesConsumer.ingest(message);
      }
      return message.ack();
    } catch (Exception e) {
      var text = String.format("Failed to ingest TMDB %s %d", command.type(), command.tmdbId());
      LOGGER.error(text, e); 
      sendStatus(IngestStatus.FAILED, message, text);
      return message.nack(e);      
    } finally {
      MDC.remove(LoggingDecorator.CORRELATION_ID);
    }
  }
  
  /// Publishes a STARTED status event for the inbound message.
  ///
  /// Computes the time the message spent waiting in the queue using
  /// the [Configuration#JOB_START_TIME] header set by the gateway,
  /// and includes it in the event's message field for diagnostics.
  private void sendStartedStatus(Message<IngestMedia> message) {
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
        .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
    var start = Instant.parse(metadata.getHeaders().get(Configuration.JOB_START_TIME).toString());
    var et = Duration.between(start, Instant.now());
    var cmd = message.getPayload();
    var text = String.format("Ingest job for TMDB %s %d sat in the ingest-media queue for %d ms", 
        cmd.type(), cmd.tmdbId(), et.toMillis());
    LOGGER.info(text);
    sendStatus(IngestStatus.STARTED, message, text);  
  }
  
  /// Publishes a status event with an arbitrary status and message.
  ///
  /// The correlation ID is read from message metadata so the event
  /// is tied back to the original ingest job.
  ///
  /// @param status the status to publish
  /// @param message the originating inbound message; used for correlation and payload
  /// @param text   a human-readable description of the transition
  private void sendStatus(IngestStatus status, Message<IngestMedia> message, String text) {
    var command = message.getPayload();
    var correlationId = MessageMetadata.getCorrelationId(message);
    producer.send(IngestStatusChanged.builder()
        .id(correlationId)
        .tmdbId(command.tmdbId())
        .status(status)
        .source(IngestSource.MEDIA)
        .type(command.type())
        .message(text)
        .build());
  }
}
