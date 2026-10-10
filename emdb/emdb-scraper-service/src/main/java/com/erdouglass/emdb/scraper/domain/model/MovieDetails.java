package com.erdouglass.emdb.scraper.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Builder;

@Builder
public record MovieDetails(
    TmdbId tmdbId,
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) {

  public MovieDetails {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
  }   
}
