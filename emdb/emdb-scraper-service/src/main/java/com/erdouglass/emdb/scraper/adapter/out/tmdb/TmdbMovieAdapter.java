package com.erdouglass.emdb.scraper.adapter.out.tmdb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.scraper.application.port.out.MovieScraper;
import com.erdouglass.emdb.scraper.domain.model.Movie;
import com.erdouglass.emdb.shared.kernel.TmdbId;

/// Anti-corruption layer between TMDB and the domain.
@ApplicationScoped
class TmdbMovieAdapter implements MovieScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieAdapter.class);
  private static final String CREDITS = "credits";
  private static final String NULL_LANGUAGE = "xx"; 
  
  @Inject
  @RestClient
  TmdbClient client;

  @Override
  public Movie scrape(TmdbId tmdbId) {
    var tmdbMovie = client.findMovieById(tmdbId.value(), CREDITS);
    var score = tmdbMovie.vote_count() > 0 ? tmdbMovie.vote_average() : null;
    var originalLanguage = tmdbMovie.original_language().equals(NULL_LANGUAGE) ? null 
                         : tmdbMovie.original_language();
    var movie = Movie.builder()
        .tmdbId(TmdbId.of(tmdbMovie.id()))
        .title(tmdbMovie.title())
        .releaseDate(tmdbMovie.release_date())
        .score(score)
        .originalLanguage(originalLanguage)
        .overview(tmdbMovie.overview())
        .build(); 
    LOGGER.infof("Extracted: %s", movie);
    return movie;
  }
}
