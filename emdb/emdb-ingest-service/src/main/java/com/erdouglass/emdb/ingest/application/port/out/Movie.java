package com.erdouglass.emdb.ingest.application.port.out;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.Builder;

@Builder
public record Movie(
    Integer tmdbId,
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) {
  
  public Movie {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
  } 
}
