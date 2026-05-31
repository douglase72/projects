package com.erdouglass.emdb.ingest.scraper.series;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.scraper.Scraper;
import com.erdouglass.emdb.media.series.SaveSeries;

/// [Scraper] implementation that fetches series from TMDB and emits the
/// resulting [SaveSeries] commands on the `save-series-out` channel.
@ApplicationScoped
class SeriesScraper extends Scraper<SaveSeries> {
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @RestClient
  SeriesClient client;
  
  @Inject
  @Channel("save-series-out") 
  Emitter<SaveSeries> emitter;

  /// Calls TMDB for the given series id (appending `aggregate_credits`) and
  /// builds the corresponding [SaveSeries] command.  
  @Override
  protected SaveSeries extract(int tmdbId) {
    var series = client.findById(tmdbId, CREDITS);
    return null;
  }

  @Override
  protected Emitter<SaveSeries> getEmitter() {
    return emitter;
  }
}
