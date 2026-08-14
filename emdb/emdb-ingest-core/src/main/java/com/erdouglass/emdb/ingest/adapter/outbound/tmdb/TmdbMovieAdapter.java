package com.erdouglass.emdb.ingest.adapter.outbound.tmdb;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.outbound.Movie;
import com.erdouglass.emdb.ingest.application.port.outbound.MovieSource;
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
    var movie = client.findMovieById(tmdbId.value(), CREDITS);
    var score = movie.vote_count() > 0 ? movie.vote_average() : null;
    var originalLanguage = movie.original_language().equals(NULL_LANGUAGE) ? null 
                         : movie.original_language();
    return new Movie(
        tmdbId, 
        movie.title(), 
        movie.release_date().filter(r -> !r.isBlank()).map(DateTimeFactory::from),
        Optional.ofNullable(score),
        Optional.ofNullable(originalLanguage),
        movie.overview().filter(o -> !o.isBlank()));
  }
}
