package com.erdouglass.emdb.media.messaging;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.event.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatus;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusContext;
import com.erdouglass.emdb.media.annotation.MessageMetadata;
import com.erdouglass.emdb.media.annotation.UpdateIngestStatus;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;
import com.erdouglass.emdb.scraper.service.TmdbMovieScraper;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieConsumer {
  private static final String ROUTE_KEY = "movie.dlq";
  
  @Inject
  @Channel("movie-dlq-out")
  Emitter<SaveMovie> emitter;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  TmdbMovieScraper scraper;
  
  @Inject
  MovieService service;
  
  @Inject 
  IngestStatusContext statusContext;
  
  @Inject
  CommandValidator validator;
  
  @UpdateIngestStatus
  public IngestStatusChanged ingest(Message<IngestMedia> message) {
    var correlationId = MessageMetadata.getCorrelationId(message);
    statusContext.setCorrelationId(correlationId);
    var tmdbId = message.getPayload().tmdbId();
    var command = service.findByTmdbId(tmdbId, null)
        .map(m -> scraper.extract(mapper.toSaveMovie(m)))
        .orElseGet(() -> scraper.extract(SaveMovie.builder().tmdbId(tmdbId).build()));
    
    try {
      validator.validate(command);
      var start = Instant.now();
      var movie = service.save(command).entity();
      var et = Duration.between(start, Instant.now()).toMillis();
      return IngestStatusChanged.builder()
          .id(correlationId)
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
          .withCorrelationId(correlationId.toString())
          .withHeader("X-Event-Type", command.getClass().getSimpleName())
          .build()));       
      throw e;    
    }
  }
}
