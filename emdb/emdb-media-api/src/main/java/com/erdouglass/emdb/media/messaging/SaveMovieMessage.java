package com.erdouglass.emdb.media.messaging;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.Builder;

@Builder
public record SaveMovieMessage(
    Integer tmdbId,
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) {

  public SaveMovieMessage {
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(title, "title is required");
  }
}
