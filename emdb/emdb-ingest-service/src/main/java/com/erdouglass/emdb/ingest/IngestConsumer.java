package com.erdouglass.emdb.ingest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.movie.MovieScraper;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

/// Consumes [IngestMedia] commands from the RabbitMQ ingest-media queue.
///
/// The queue is configured with max-outstanding-messages set to one to avoid 
/// overwhelming the TMDB API with concurrent requests.
@ApplicationScoped
public class IngestConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestConsumer.class);
  
  @Inject
  MovieScraper movieScraper;
  
  /// Processes a single [IngestMedia] message from the ingest-media queue.
  ///
  /// @param message the inbound message containing the [IngestMedia] payload
  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public Uni<Void> onMessage(Message<IngestMedia> message) {
    try {
      movieScraper.scrape(message);
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      LOGGER.error("Failed to ingest media", e);
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
}
