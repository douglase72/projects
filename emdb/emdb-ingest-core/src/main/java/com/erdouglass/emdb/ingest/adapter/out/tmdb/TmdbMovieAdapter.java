package com.erdouglass.emdb.ingest.adapter.out.tmdb;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.application.dto.Movie;
import com.erdouglass.emdb.ingest.application.port.out.MovieSource;
import com.erdouglass.emdb.media.TmdbId;

@ApplicationScoped
class TmdbMovieAdapter implements MovieSource {
  private static final String CREDITS = "credits";
  private static final String NULL_LANGUAGE = "xx";
  
  @Inject
  @RestClient
  TmdbClient client;

  @Override
  public Movie extract(TmdbId tmdbId) {
    var tmdbMovie = client.findMovieById(tmdbId.value(), CREDITS);
    var score = tmdbMovie.vote_count() > 0 ? tmdbMovie.vote_average() : null;
    var originalLanguage = tmdbMovie.original_language().equals(NULL_LANGUAGE) ? null 
                         : tmdbMovie.original_language();
    var movie = Movie.builder()
        .tmdbId(TmdbId.of(tmdbMovie.id()))
        .title(tmdbMovie.title())
        .releaseDate(Optional.ofNullable(tmdbMovie.release_date()).filter(r -> !r.isBlank()))
        .score(Optional.ofNullable(score))
        .originalLanguage(Optional.ofNullable(originalLanguage))
        .overview(Optional.ofNullable(tmdbMovie.overview()).filter(r -> !r.isBlank()))
        .build();
    return movie;
  }
}
