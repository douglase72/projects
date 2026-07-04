package com.erdouglass.emdb.ingest.core;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.IngestEvent;
import com.erdouglass.emdb.ingest.IngestFailed;
import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.core.movie.MovieIngestHandler;
import com.erdouglass.emdb.ingest.core.person.PersonIngestHandler;
import com.erdouglass.emdb.ingest.core.series.SeriesIngestHandler;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
class IngestConsumer {
  private static final Logger LOGGER = Logger.getLogger(IngestConsumer.class);
  
  @Inject
  Event<IngestEvent> emitter;
  
  @Inject
  MovieIngestHandler movieHandler;
  
  @Inject
  PersonIngestHandler personHandler;
  
  @Inject
  SeriesIngestHandler seriesHandler;

  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public Uni<Void> onMessage(Message<IngestMedia> message) {
    var command = message.getPayload();
    try {
      switch (command.type()) {
        case MOVIE -> movieHandler.ingest(message);
        case PERSON -> personHandler.ingest(message);
        case SERIES -> seriesHandler.ingest(message);
      }
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      var msg = String.format("Ingest of TMDB %s %d failed.", command.type(), command.tmdbId());
      LOGGER.errorf(e, msg);
      emitter.fire(new IngestFailed(IngestUtilities.correlationId(message), msg, command.type()));
      return Uni.createFrom().completionStage(message.nack(e));
    }    
  }
}
