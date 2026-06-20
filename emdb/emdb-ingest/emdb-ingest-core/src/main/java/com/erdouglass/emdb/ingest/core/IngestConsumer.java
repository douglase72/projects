package com.erdouglass.emdb.ingest.core;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.core.movie.MovieIngestConsumer;
import com.erdouglass.emdb.ingest.core.person.PersonIngestConsumer;
import com.erdouglass.emdb.ingest.core.series.SeriesIngestConsumer;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
class IngestConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestConsumer.class);
  
  @Inject
  MovieIngestConsumer movieConsumer;
  
  @Inject
  PersonIngestConsumer personConsumer;
  
  @Inject
  SeriesIngestConsumer seriesConsumer;

  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public Uni<Void> onMessage(Message<IngestMedia> message) {
    try {
      var command = message.getPayload();
      switch (command.type()) {
        case MOVIE -> movieConsumer.ingest(message);
        case PERSON -> personConsumer.ingest(message);
        case SERIES -> seriesConsumer.ingest(message);
      }
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      LOGGER.errorf(e, "Failed to ingest media");
      return Uni.createFrom().completionStage(message.nack(e));
    }    
  }
}
