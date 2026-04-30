package com.erdouglass.emdb.media.messaging;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.event.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatus;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusContext;
import com.erdouglass.emdb.media.annotation.MessageMetadata;
import com.erdouglass.emdb.media.annotation.UpdateIngestStatus;
import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.service.SeriesService;
import com.erdouglass.emdb.scraper.service.TmdbSeriesScraper;

@ApplicationScoped
public class SeriesConsumer {

  @Inject
  SeriesMapper mapper;
  
  @Inject
  TmdbSeriesScraper scraper;
  
  @Inject
  SeriesService service;
  
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
        .map(m -> scraper.extract(mapper.toSaveSeries(m)))
        .orElseGet(() -> scraper.extract(SaveSeries.builder().tmdbId(tmdbId).build()));    
    
    try {
      validator.validate(command);
      var start = Instant.now();
      var series = service.save(command).entity();
      var et = Duration.between(start, Instant.now()).toMillis();      
      return IngestStatusChanged.builder()          
          .id(correlationId)
          .tmdbId(message.getPayload().tmdbId())
          .status(IngestStatus.LOADED)
          .source(IngestSource.MEDIA)
          .type(MediaType.SERIES)
          .message(String.format("Ingest job for TMDB series %d persisted in %d ms", series.getTmdbId(), et))
          .emdbId(series.getId())
          .name(series.getTitle())          
          .build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
