package com.erdouglass.emdb.media;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import com.erdouglass.common.util.DateTime;

public record MovieExtractedEvent(
    TmdbId tmdbId,
    String title,
    Optional<DateTime> releaseDate,
    Optional<BigDecimal> score,
    Optional<String> originalLanguage,
    Optional<String> overview) {

  public MovieExtractedEvent {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(releaseDate, "releaseDate must not be null");
    Objects.requireNonNull(score, "score must not be null");
    Objects.requireNonNull(originalLanguage, "originalLanguage must not be null");
    Objects.requireNonNull(overview, "overview must not be null");
  }
}
