package com.erdouglass.emdb.ingest.adapter.outbound.tmdb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.application.port.outbound.MovieDto;
import com.erdouglass.emdb.media.LanguageCode;
import com.erdouglass.emdb.media.Score;
import com.erdouglass.emdb.media.Title;
import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.movie.ReleaseDate;

/// Extract movie details from TMDB.
@ApplicationScoped
class TmdbMovieAdapter {
  private static final String CREDITS = "credits";
  private static final String NULL_LANGUAGE = "xx";
  
  @Inject
  @RestClient
  TmdbClient client;

  public MovieDto extract(int tmdbId) {
    var movie = client.findMovieById(tmdbId, CREDITS);
    var releaseDate = movie.release_date().filter(r -> !r.isBlank()).map(ReleaseDate::from).orElse(null);
    var score = movie.vote_count() > 0 ? Score.of(movie.vote_average()) : null;
    var originalLanguage = movie.original_language().equals(NULL_LANGUAGE) ? null 
                         : LanguageCode.of(movie.original_language());
    return MovieDto.builder()
        .tmdbId(TmdbId.of(tmdbId))
        .title(Title.of(movie.title()))
        .releaseDate(releaseDate)
        .score(score)
        .originalLanguage(originalLanguage)
        .build();
  }
}
