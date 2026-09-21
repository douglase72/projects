package com.erdouglass.emdb.ingest.adapter.out.tmdb;

import java.math.BigDecimal;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

@ApplicationScoped
class TmdbMovieScraper {

  public Movie scrape(TmdbId tmdbId) {
    return Movie.builder()
        .tmdbId(78)
        .title("Blade Runner")
        .releaseDate("1982-06-25")
        .score(BigDecimal.valueOf(7.893))
        .originalLanguage("en")
        .overview("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.")
        .build();
  }
}
