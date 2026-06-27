package com.erdouglass.emdb.ingest.core.series;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.core.Log;
import com.erdouglass.emdb.ingest.core.Scraper;
import com.erdouglass.emdb.ingest.core.image.ImageScraper;
import com.erdouglass.emdb.media.series.SaveSeries;

@ApplicationScoped
class SeriesScraper extends Scraper<Series> {
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @RestClient
  TmdbSeriesClient client;
  
  @Inject
  ImageScraper imageScraper;
  
  @Inject
  TmdbSeriesMapper mapper;
  
  @Inject
  SeriesRepository repository;
  
  @Log
  @Transactional
  public SaveSeries scrape(@NotNull @Positive Integer tmdbId) {
    var tmdbSeries = client.findById(tmdbId, CREDITS);   
    var existing = repository.findById(tmdbId).orElse(null);
    var backdrop = resolveImage(existing, tmdbSeries.backdrop_path(),
        Series::getTmdbBackdrop, Series::getEmdbBackdrop);
    var poster = resolveImage(existing, tmdbSeries.poster_path(),
        Series::getTmdbPoster, Series::getEmdbPoster);
    var series = existing != null ? existing : new Series(tmdbId);
    series.setEmdbBackdrop(nameOf(backdrop));
    series.setTmdbBackdrop(tmdbSeries.backdrop_path());
    series.setEmdbPoster(nameOf(poster));
    series.setTmdbPoster(tmdbSeries.poster_path());
    repository.save(series); 
    return mapper.toSaveSeries(tmdbSeries, backdrop, poster);
  }
}
