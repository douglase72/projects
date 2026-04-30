package com.erdouglass.emdb.scraper.service;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.Image;
import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.event.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatus;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusContext;
import com.erdouglass.emdb.scraper.annotation.UpdateScraperStatus;
import com.erdouglass.emdb.scraper.client.TmdbSeriesClient;
import com.erdouglass.emdb.scraper.mapper.TmdbSeriesMapper;

@ApplicationScoped
public class TmdbSeriesScraper {
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @RestClient
  TmdbSeriesClient client;
  
  @Inject
  TmdbSeriesMapper mapper;
  
  @Inject 
  IngestStatusContext statusContext;

  @UpdateScraperStatus
  public SaveSeries extract(@NotNull SaveSeries command) {
    var tmdbSeries = client.findById(command.tmdbId(), CREDITS);
    
    // Update the series backdrop and poster if they changed.
    var backdrop = Image.of(UUID.randomUUID(), tmdbSeries.backdrop_path());
    var poster = Image.of(UUID.randomUUID(), tmdbSeries.poster_path());
    
    statusContext.set(IngestStatusChanged.builder()
        .tmdbId(tmdbSeries.id())
        .status(IngestStatus.EXTRACTED)
        .source(IngestSource.SCRAPER)
        .type(MediaType.SERIES)
        .name(tmdbSeries.name())
        .message(String.format("Ingest Job for TMDB series %d fetched from TMDB", tmdbSeries.id()))
        .build());
    var cmd = mapper.toSaveSeries(tmdbSeries, backdrop, poster);
    return cmd;
  }
}
