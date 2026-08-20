package com.erdouglass.emdb.ingest.application.dto;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.TmdbId;

import lombok.Builder;

@Builder
public record Movie(
    TmdbId tmdbId,
    String title,
    Optional<String> releaseDate,
    Optional<BigDecimal> score,
    Optional<String> originalLanguage,
    Optional<String> overview) {

  public Movie {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
  }
}
