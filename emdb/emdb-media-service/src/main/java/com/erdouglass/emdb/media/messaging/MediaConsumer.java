package com.erdouglass.emdb.media.messaging;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.messaging.LoggingDecorator;

import io.smallrye.common.annotation.RunOnVirtualThread;

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
  
  @Inject
  MovieConsumer movieConsumer;
  
  @Inject
  PersonConsumer personConsumer;
  
  @Inject
  SeriesConsumer seriesConsumer;
  
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
      switch (command.type()) {
        case MOVIE -> movieConsumer.ingest(message);
        case PERSON -> personConsumer.ingest(message);
        case SERIES -> seriesConsumer.ingest(message);
      };
      return message.ack();
    } catch (Exception e) {
      return message.nack(e);      
    } finally {
      MDC.remove(LoggingDecorator.CORRELATION_ID);
    }
  }
}
