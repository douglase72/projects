package com.erdouglass.emdb.scraper.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.media.api.SaveMovie;
import com.erdouglass.emdb.scraper.Scraper;

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

  /// Calls TMDB for the given movie id (appending `credits`) and builds the
  /// corresponding [SaveMovie] command.  
  @Override
  protected SaveMovie extract(final int tmdbId) {
    var movie = client.findById(tmdbId, CREDITS);
    var command = SaveMovie.builder()
        .tmdbId(movie.id())
        .title(movie.title())
        .releaseDate(movie.release_date())
        .build();
    return command;
  }

  @Override
  protected Emitter<SaveMovie> getEmitter() {
    return emitter;
  }
}
