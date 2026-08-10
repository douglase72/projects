package com.erdouglass.emdb.ingest.application.port.outbound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.TmdbId;

public record Movie(
    TmdbId tmdbId,
    String title,
    Optional<LocalDate> releaseDate,
    Optional<BigDecimal> score,
    Optional<String> originalLanguage) {

  public Movie {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(releaseDate, "releaseDate must not be null");
    Objects.requireNonNull(score, "score must not be null");
    Objects.requireNonNull(originalLanguage, "originalLanguage must not be null");
  }
}
