package com.erdouglass.emdb.ingest.adapter.outbound.tmdb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

public record TmdbMovieResponse(    
    Integer id,
    String title,
    Optional<String> release_date,
    BigDecimal vote_average,
    Integer vote_count,
    String original_language) {
  private static final int SCALE = 3;

  public TmdbMovieResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(vote_average, "vote_average must not be null");
    Objects.requireNonNull(vote_count, "vote_count must not be null");
    Objects.requireNonNull(original_language, "original_language must not be null");
    vote_average = vote_average.setScale(SCALE, RoundingMode.HALF_UP);
  }
}
