package com.erdouglass.emdb.media.messaging;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.common.api.MediaType;
import com.erdouglass.emdb.common.api.command.IngestMedia;
import com.erdouglass.emdb.common.api.messaging.IngestSource;
import com.erdouglass.emdb.common.api.messaging.IngestStatus;
import com.erdouglass.emdb.common.api.messaging.IngestStatusChanged;
import com.erdouglass.emdb.media.annotation.CorrelationContext;
import com.erdouglass.emdb.media.annotation.UpdateStatus;
import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.service.CommandValidator;
import com.erdouglass.emdb.media.service.MovieCrudService;
import com.erdouglass.emdb.media.service.TmdbMovieScraper;
import com.erdouglass.emdb.media.utils.MessageMetadata;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieConsumer {
  private static final String ROUTE_KEY = "movie.dlq";
  
  @Inject
  CorrelationContext correlation;
  
  @Inject
  @Channel("movie-dlq-out")
  Emitter<SaveMovie> emitter;  
  
  @Inject
  TmdbMovieScraper scraper;
  
  @Inject
  MovieCrudService service;
  
  @Inject
  CommandValidator validator;
  
  @UpdateStatus
  public IngestStatusChanged ingest(Message<IngestMedia> message) {
    correlation.setId(MessageMetadata.getCorrelationId(message));
    var tmdbId = message.getPayload().tmdbId();
    var command = service.findByTmdbId(tmdbId, null)
        .map(m -> scraper.extract(m))
        .orElseGet(() -> {
          var movie = new Movie();
          movie.setTmdbId(tmdbId);
          return scraper.extract(movie);
        });
        
    try {
      var start = Instant.now();
      validator.validate(command);
      var movie = service.save(command).entity();
      var et = Duration.between(start, Instant.now()).toMillis();
      return IngestStatusChanged.builder()
          .id(correlation.getId())
          .tmdbId(movie.getTmdbId())
          .status(IngestStatus.LOADED)
          .source(IngestSource.MEDIA)
          .type(MediaType.MOVIE)
          .message(String.format("Ingest job for TMDB movie %d persisted in %d ms", movie.getTmdbId(), et))
          .emdbId(movie.getId())
          .name(movie.getTitle())
          .build();
    } catch (ConstraintViolationException e) {
      emitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .withCorrelationId(correlation.getId().toString())
          .withHeader("X-Event-Type", command.getClass().getSimpleName())
          .build())); 
      throw e;     
    }
  }
}
