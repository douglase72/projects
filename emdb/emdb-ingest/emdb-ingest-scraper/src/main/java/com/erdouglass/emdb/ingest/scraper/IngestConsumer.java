package com.erdouglass.emdb.ingest.scraper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.api.IngestMedia;
import com.erdouglass.emdb.media.api.SaveMovie;
import com.erdouglass.emdb.media.api.SavePerson;
import com.erdouglass.emdb.media.api.SaveSeries;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

/// Consumes [IngestMedia] commands from the RabbitMQ ingest-media queue.
///
/// The queue is configured with max-outstanding-messages=1 to ensure each ingest
/// message is processed one at a time. This allows a rate limit to be imposed on
/// each ingest process respecting the TMDB limit.
@ApplicationScoped
class IngestConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestConsumer.class);

  @Inject
  Scraper<SaveMovie> movieScraper;

  @Inject
  Scraper<SavePerson> personScraper;

  @Inject
  Scraper<SaveSeries> seriesScraper;

  /// Dispatches an incoming [IngestMedia] message to the scraper matching its
  /// [IngestMedia.IngestType], then acknowledges the message.
  ///
  /// Any exception thrown while scraping is logged and the message is negatively
  /// acknowledged. This uses the default RabbitMQ configuration in Quarkus which
  /// is to drop nack'd messages.
  ///
  /// @param message the incoming RabbitMQ message carrying the ingest command
  /// @return a [Uni] that completes once the message has been ack'd or nack'd
  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public Uni<Void> onMessage(final Message<IngestMedia> message) {
    try {
      var command = message.getPayload();
      switch (command.type()) {
        case MOVIE -> movieScraper.scrape(message);
        case SERIES -> seriesScraper.scrape(message);
        case PERSON -> personScraper.scrape(message);
      }
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      LOGGER.errorf(e, "Failed to ingest media");
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
}