package com.erdouglass.emdb.ingest.series;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.series.SaveSeries;
import com.erdouglass.emdb.ingest.Scraper;

@ApplicationScoped
public class SeriesScraper extends Scraper<SaveSeries> {
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @RestClient
  SeriesClient client;
  
  @Inject
  @Channel("save-series-out") 
  Emitter<SaveSeries> emitter;
  
  @Inject
  SeriesMapper mapper;
  
  @Override
  protected SaveSeries getCommand(int tmdbId) {
    var tmdbSeries = client.findById(tmdbId, CREDITS);
    var command = mapper.toSaveSeries(tmdbSeries);
    return command;
  }
  
  @Override
  protected Emitter<SaveSeries> getEmitter() {
    return emitter;
  }
}
