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
import com.erdouglass.emdb.media.entity.Credit;
import com.erdouglass.emdb.media.entity.Movie_;
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
  
  @Override
  public void ingest(@NotNull @Positive Integer tmdbId) {
    var existingMovie = service.findByTmdbId(tmdbId, Movie_.CREDITS);
    var command = existingMovie
        .map(m -> scraper.extract(mapper.toSaveMovie(m)))
        .orElseGet(() -> scraper.extract(SaveMovie.builder().tmdbId(tmdbId).build()));
    
    try {
      validate(command);
      var result = service.save(command);
      existingMovie.ifPresent(m -> {
        deleteOldImages(m, result.entity());
        var existingPeople = m.getCredits().stream().map(Credit::getPerson).toList();
        var newPeople = result.entity().getCredits().stream().map(Credit::getPerson).toList();
        deleteOldImages(existingPeople, newPeople);
      });
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
