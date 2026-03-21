package com.erdouglass.emdb.media.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;
import com.erdouglass.emdb.scraper.service.TmdbMovieScraper;
import com.erdouglass.webservices.LoggingDecorator;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieConsumer extends Consumer {
  private static final String ROUTE_KEY = "movie.invalid";
  
  @Inject
  @Channel("movie-dlq-out")
  Emitter<SaveMovie> dlqEmitter;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  TmdbMovieScraper scraper;
  
  @Inject
  MovieService service;
  
  public void ingest(@NotNull @Positive Integer tmdbId) {
    var command = service.findByTmdbId(tmdbId)
        .map(m -> scraper.extract(mapper.toSaveMovie(m)))
        .orElseGet(() -> scraper.extract(tmdbId));
    
    try {
      validate(command);
      service.save(command);
    } catch (Exception e) {
      dlqEmitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .withCorrelationId((String) MDC.get(LoggingDecorator.CORRELATION_ID))
          .withHeader("X-Event-Type", command.getClass().getSimpleName())
          .build()));      
      throw new RuntimeException(e);
    }    
  }

}
