package com.erdouglass.emdb.ingest.movie;

import java.util.NoSuchElementException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.common.messaging.LoggingDecorator;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.command.SaveMovie;
import com.erdouglass.emdb.ingest.IngestMedia;

import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieScraper {
  private static final Logger LOGGER = Logger.getLogger(MovieScraper.class);
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  MovieClient client;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  @Channel("save-movie-out") 
  Emitter<SaveMovie> emitter;

  /// Extract the movie details from TMDB and send the [SaveMovie] command to 
  /// the media service.
  public void scrape(Message<IngestMedia> message) {
    try {
      int tmdbId = message.getPayload().tmdbId();
      var tmdbMovie = client.findById(tmdbId, CREDITS); 
      var command = mapper.toSaveMovie(tmdbMovie);
      LOGGER.infof("command: %s", command);
      
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
    } catch (ConstraintViolationException e) {
      // TODO: Send to Movie Dead Letter Queue
      throw e;
    }
  }
}
