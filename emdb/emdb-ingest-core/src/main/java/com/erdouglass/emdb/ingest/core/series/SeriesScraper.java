package com.erdouglass.emdb.ingest.core.series;

import java.util.Comparator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.core.Scraper;
import com.erdouglass.emdb.ingest.core.image.ImageScraper;
import com.erdouglass.emdb.ingest.logging.Log;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SaveSeries.CastCredit;
import com.erdouglass.emdb.media.series.SaveSeries.Credits;

@ApplicationScoped
class SeriesScraper extends Scraper<Series> {
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;
  
  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;
  
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
  public SaveSeries scrape(Message<IngestMedia> message) {
    var tmdbId = message.getPayload().tmdbId();
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
    return limitCredits(mapper.toSaveSeries(tmdbSeries, backdrop, poster));
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
