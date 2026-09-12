package com.erdouglass.emdb.media;

import java.math.BigDecimal;
import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.emdb.common.TmdbId;

import lombok.Builder;

@Builder
public record SaveMovieCommand(
    TmdbId tmdbId,
    String title,
    DateTime releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) {

  public SaveMovieCommand {
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(title, "title is required");
  }
}
