package com.erdouglass.emdb.ingest.adapter.out.tmdb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

/// Anti-corruption layer between the TMDB API and the Ingest domain.
@ApplicationScoped
class TmdbMovieScraper {
  private static final String CREDITS = "credits";
  private static final String NULL_LANGUAGE = "xx"; 
  
  @Inject
  @RestClient
  TmdbClient client;

  public Movie scrape(TmdbId tmdbId) {
    var tmdbMovie = client.findMovieById(tmdbId.value(), CREDITS);
    var score = tmdbMovie.vote_count() > 0 ? tmdbMovie.vote_average() : null;
    var originalLanguage = tmdbMovie.original_language().equals(NULL_LANGUAGE) ? null 
                         : tmdbMovie.original_language();
    var movie = Movie.builder()
        .tmdbId(tmdbMovie.id())
        .title(tmdbMovie.title())
        .releaseDate(tmdbMovie.release_date())
        .score(score)
        .originalLanguage(originalLanguage)
        .overview(tmdbMovie.overview())
        .build();
    return movie;    
  }
}
