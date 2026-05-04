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
import com.erdouglass.emdb.media.api.command.SaveSeries;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.service.CommandValidator;
import com.erdouglass.emdb.media.service.SeriesCrudService;
import com.erdouglass.emdb.media.service.TmdbSeriesScraper;
import com.erdouglass.emdb.media.utils.MessageMetadata;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class SeriesConsumer {
  private static final String ROUTE_KEY = "series.dlq";
  
  @Inject
  CorrelationContext correlation;
  
  @Inject
  @Channel("series-dlq-out")
  Emitter<SaveSeries> emitter;  
  
  @Inject
  TmdbSeriesScraper scraper;
  
  @Inject
  SeriesCrudService service;
  
  @Inject
  CommandValidator validator;
  
  @UpdateStatus
  public IngestStatusChanged ingest(Message<IngestMedia> message) {
    correlation.setId(MessageMetadata.getCorrelationId(message));
    var tmdbId = message.getPayload().tmdbId();
    var command = service.findByTmdbId(tmdbId, null)
        .map(s -> scraper.extract(s))
        .orElseGet(() -> {
          var series = new Series();
          series.setTmdbId(tmdbId);
          return scraper.extract(series);
        });
        
    try {
      var start = Instant.now();
      validator.validate(command);
      var series = service.save(command).entity();
      var et = Duration.between(start, Instant.now()).toMillis();
      return IngestStatusChanged.builder()
          .id(correlation.getId())
          .tmdbId(series.getTmdbId())
          .status(IngestStatus.LOADED)
          .source(IngestSource.MEDIA)
          .type(MediaType.SERIES)
          .message(String.format("Ingest job for TMDB series %d persisted in %d ms", series.getTmdbId(), et))
          .emdbId(series.getId())
          .name(series.getTitle())
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
