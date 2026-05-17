package com.erdouglass.emdb.ingest.movie;

import java.util.NoSuchElementException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.common.messaging.LoggingDecorator;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.command.SaveMovie;
import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.Scraper;

import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Fetches movie details from TMDB and publishes a [SaveMovie] command to
/// the media service.
///
/// This is the ingest-side counterpart to
/// [com.erdouglass.emdb.media.movie.MovieConsumer]: this side reads from
/// TMDB and writes to the message broker; the other side reads from the
/// broker and writes to the database. Splitting the work this way keeps
/// the slow external HTTP call out of the database transaction.
@ApplicationScoped
class MovieScraper implements Scraper {
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  MovieClient client;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  @Channel("save-movie-out") 
  Emitter<SaveMovie> emitter;

  /// Extracts movie details from TMDB and sends a [SaveMovie] command to
  /// the media service.
  ///
  /// Propagates the inbound message's correlation ID and `START_TIME`
  /// header onto the outbound command so end-to-end latency and tracing
  /// stay coherent across the ingest → save pipeline.
  ///
  /// @param message the inbound [IngestMedia] message; must carry RabbitMQ
  ///                metadata including a correlation ID and `START_TIME` header
  @Override
  public void scrape(Message<IngestMedia> message) {
    int tmdbId = message.getPayload().tmdbId();
    var tmdbMovie = client.findById(tmdbId, CREDITS); 
    var command = mapper.toSaveMovie(tmdbMovie);
    
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
        .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
    var correlationId = metadata.getCorrelationId()
        .orElseThrow(() -> new NoSuchElementException("No correlation id.")); 
    var startTime = metadata.getHeaders().get(Configuration.START_TIME).toString();
    emitter.send(Message.of(command)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withCorrelationId(correlationId)
            .withHeader(Configuration.START_TIME, startTime)
            .withHeader(LoggingDecorator.EVENT_TYPE, command.getClass().getSimpleName())
            .build()));
  }
}
