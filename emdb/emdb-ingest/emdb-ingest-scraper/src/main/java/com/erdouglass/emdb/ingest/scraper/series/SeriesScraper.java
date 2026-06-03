package com.erdouglass.emdb.ingest.scraper.series;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.scraper.Scraper;
import com.erdouglass.emdb.ingest.scraper.image.ImageScraper;
import com.erdouglass.emdb.ingest.scraper.internal.CreditLimiter;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SaveSeries.Credits;

/// [Scraper] implementation that fetches series from TMDB and emits the
/// resulting [SaveSeries] commands on the `save-series-out` channel.
@ApplicationScoped
class SeriesScraper extends Scraper<SaveSeries> {
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @RestClient
  SeriesClient client;
  
  @Inject
  CreditLimiter creditLimiter;
  
  @Inject
  @Channel("save-series-out") 
  Emitter<SaveSeries> emitter;
  
  @Inject
  ImageScraper imageScraper;
  
  @Inject
  SeriesMapper mapper;

  /// Calls TMDB for the given series id (appending `aggregate_credits`) and
  /// builds the corresponding [SaveSeries] command.  
  @Override
  protected SaveSeries extract(final int tmdbId) {
    var series = client.findById(tmdbId, CREDITS);
    var backdrop = imageScraper.extract(series.backdrop_path());
    var poster = imageScraper.extract(series.poster_path());
    var command = mapper.toSaveSeries(series, backdrop, poster);
    var credits = command.credits();
    return null;
  }

  @Override
  protected Emitter<SaveSeries> getEmitter() {
    return emitter;
  }
}
