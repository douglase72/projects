package com.erdouglass.emdb.scraper.adapter.out.tmdb;

import java.math.BigDecimal;
import java.util.Objects;

public record TmdbMovieResponse(
    Integer id,
    String title,
    String release_date,
    BigDecimal vote_average,
    Integer vote_count,
    String original_language,
    String overview) {

  public TmdbMovieResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(vote_average, "vote_average must not be null");
    Objects.requireNonNull(vote_count, "vote_count must not be null");
    Objects.requireNonNull(original_language, "original_language must not be null");
  }  
}