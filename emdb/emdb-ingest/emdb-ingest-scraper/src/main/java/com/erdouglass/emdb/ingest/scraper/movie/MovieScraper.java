package com.erdouglass.emdb.ingest.scraper.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.scraper.Scraper;
import com.erdouglass.emdb.media.api.command.SaveMovie;

/// [Scraper] implementation that fetches movies from TMDB and emits the
/// resulting [SaveMovie] commands on the `save-movie-out` channel.
@ApplicationScoped
class MovieScraper extends Scraper<SaveMovie> {
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  MovieClient client;
  
  @Inject
  @Channel("save-movie-out") 
  Emitter<SaveMovie> emitter;
  
  @Inject
  MovieMapper mapper;

  /// Calls TMDB for the given movie id (appending `credits`) and builds the
  /// corresponding [SaveMovie] command.  
  @Override
  protected SaveMovie extract(final int tmdbId) {
    var movie = client.findById(tmdbId, CREDITS);
    var command = mapper.toSaveMovie(movie);
    return command;
  }

  @Override
  protected Emitter<SaveMovie> getEmitter() {
    return emitter;
  }
}