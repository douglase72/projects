package com.erdouglass.emdb.scraper;

import java.util.NoSuchElementException;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.LoggingDecorator;
import com.erdouglass.emdb.ingest.api.IngestMedia;
import com.erdouglass.emdb.media.api.SaveCommand;

import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Template for type-specific scrapers that turn an inbound [IngestMedia]
/// message into a downstream save command.
///
/// Subclasses provide both the extraction logic (typically a TMDB REST call)
/// via [#extract(int)] and the outbound [Emitter] via [#getEmitter()]. The
/// base class handles correlation-id propagation and metadata wiring so the
/// produced save command stays linked to the original ingest request.
///
/// @param <T> the concrete [SaveCommand] type emitted by this scraper
public abstract class Scraper<T extends SaveCommand> {

  /// Scrapes data for the given ingest message and forwards the resulting
  /// save command on the subclass's emitter.
  ///
  /// The incoming RabbitMQ correlation id is copied onto the outgoing message
  /// and an event-type header is added so logging and tracing decorators can
  /// follow the command downstream.
  ///
  /// @param message the incoming ingest message; must carry RabbitMQ metadata
  ///                with a correlation id
  /// @return the save command that was produced and emitted
  /// @throws IllegalStateException  if the message has no RabbitMQ metadata
  /// @throws NoSuchElementException if the metadata lacks a correlation id  
  @Scrape
  public T scrape(final Message<IngestMedia> message) {    
    var command = extract(message.getPayload().tmdbId());    
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
        .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
    var correlationId = metadata.getCorrelationId()
        .orElseThrow(() -> new NoSuchElementException("No correlation id."));     
    getEmitter().send(Message.of(command)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withCorrelationId(correlationId)
            .withHeader(LoggingDecorator.EVENT_TYPE, command.getClass().getSimpleName())
            .build()));
    return command;
  }
  
  /// Fetches the remote entity identified by the given TMDB id and maps it
  /// into a save command ready for publication.
  ///
  /// @param tmdbId the TMDB identifier of the entity to scrape
  /// @return the populated save command  
  protected abstract T extract(int tmdbId);
  
  /// Returns the outbound [Emitter] that the base [#scrape(Message)] method
  /// should use to publish the produced save command.
  ///
  /// @return the configured emitter for type `T`
  protected abstract Emitter<T> getEmitter();
}
