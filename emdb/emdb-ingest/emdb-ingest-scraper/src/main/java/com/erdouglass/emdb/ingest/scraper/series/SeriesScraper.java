package com.erdouglass.emdb.ingest.scraper.series;

import java.util.Comparator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.scraper.Scraper;
import com.erdouglass.emdb.ingest.scraper.image.ImageScraper;
import com.erdouglass.emdb.media.command.SaveSeries;
import com.erdouglass.emdb.media.command.SaveSeries.CastCredit;
import com.erdouglass.emdb.media.command.SaveSeries.Credits;

@ApplicationScoped
class SeriesScraper extends Scraper<SaveSeries> {
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;
  
  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;

  @Inject
  @RestClient
  SeriesClient client;
  
  @Inject
  @Channel("save-series-out") 
  Emitter<SaveSeries> emitter;
  
  @Inject
  ImageScraper imageScraper;
  
  @Inject
  SeriesMapper mapper;
  
  @Override
  protected Emitter<SaveSeries> emitter() {
    return emitter;
  }
  
  @Override
  protected SaveSeries extract(final int tmdbId) {
    var series = client.findById(tmdbId, CREDITS);
    var backdrop = imageScraper.extract(series.backdrop_path());
    var poster = imageScraper.extract(series.poster_path());
    var command = limitCredits(mapper.toSaveSeries(series, backdrop, poster));
    return command;
  }
  
  private SaveSeries limitCredits(SaveSeries command) {
    var cast = command.credits().cast().stream()
        .sorted(Comparator.comparingInt(CastCredit::order))
        .limit(castLimit)
        .toList();
    var crew = command.credits().crew().stream()
        .limit(crewLimit)
        .toList();
    return SaveSeries.builder(command)
        .credits(new Credits(cast, crew))
        .build();
  }
}
