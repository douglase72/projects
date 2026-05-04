package com.erdouglass.emdb.media.service;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.media.annotation.ExtractionStatus;
import com.erdouglass.emdb.media.api.Image;
import com.erdouglass.emdb.media.api.command.SaveSeries;
import com.erdouglass.emdb.media.client.TmdbSeriesClient;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.mapper.SeriesMapper;

@ApplicationScoped
public class TmdbSeriesScraper {
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @RestClient
  TmdbSeriesClient client;
  
  @Inject
  SeriesMapper mapper;
  
  @ExtractionStatus
  public SaveSeries extract(@NotNull Series series) {
    var tmdbSeries = client.findById(series.getTmdbId(), CREDITS);    
    var backdrop = Image.of(UUID.randomUUID(), tmdbSeries.backdrop_path());
    var poster = Image.of(UUID.randomUUID(), tmdbSeries.poster_path());    
    return mapper.toSaveSeries(tmdbSeries, backdrop, poster);
  }
}
