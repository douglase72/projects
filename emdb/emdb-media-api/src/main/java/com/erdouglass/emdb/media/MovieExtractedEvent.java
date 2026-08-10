package com.erdouglass.emdb.media;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public record MovieExtractedEvent(
    TmdbId tmdbId,
    String title,
    Optional<LocalDate> releaseDate,
    Optional<BigDecimal> score,
    Optional<String> originalLanguage) {

  public MovieExtractedEvent {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(releaseDate, "releaseDate must not be null");
    Objects.requireNonNull(score, "score must not be null");
    Objects.requireNonNull(originalLanguage, "originalLanguage must not be null");
  }
}
