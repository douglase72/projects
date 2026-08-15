package com.erdouglass.emdb.media;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public record SaveMovieCommand(
    TmdbId tmdbId,
    String title,
    Optional<String> releaseDate,
    Optional<BigDecimal> score,
    Optional<String> originalLanguage,
    Optional<String> overview) implements UpsertMovieCommand {
  
  public SaveMovieCommand {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
  }
}
