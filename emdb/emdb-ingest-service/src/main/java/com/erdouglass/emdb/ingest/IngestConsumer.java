package com.erdouglass.emdb.ingest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.movie.SaveMovie;
import com.erdouglass.emdb.common.series.SaveSeries;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

/// Consumes [IngestMedia] commands from the RabbitMQ ingest-media queue.
///
/// The queue is configured with max-outstanding-messages set to one to avoid 
/// overwhelming the TMDB API with concurrent requests.
@ApplicationScoped
class IngestConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestConsumer.class);
  
  @Inject
  Scraper<SaveMovie> movieScraper;
  
  @Inject
  Scraper<SaveSeries> seriesScraper;
  
  /// Dispatches a single [IngestMedia] message to the appropriate scraper.
  ///
  /// Currently only [MediaType#MOVIE] is handled; a future revision will
  /// route by [IngestMedia#type] to the matching scraper (e.g., series,
  /// person). Any failure during scraping nacks the message so the broker
  /// can route it to the dead-letter queue.
  ///
  /// Runs on a virtual thread because [Scraper#scrape] performs
  /// blocking HTTP and broker I/O.
  ///
  /// @param message the inbound message containing the [IngestMedia] payload
  /// @return a [Uni] that completes when the ack or nack has been dispatched
  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public Uni<Void> onMessage(Message<IngestMedia> message) {
    try {
      var command = message.getPayload();
      switch (command.type()) {
        case MOVIE -> movieScraper.scrape(message);
        case SERIES -> seriesScraper.scrape(message);
        case PERSON -> throw new IllegalArgumentException("Invalid command");
      }
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      LOGGER.errorf(e, "Failed to ingest media");
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
}
