package com.erdouglass.emdb.ingest.application.port.out;

import java.math.BigDecimal;
import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

import lombok.Builder;

@Builder
public record Movie(
    TmdbId tmdbId,
    String title,
    DateTime releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) {

  public Movie {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
  }  
}
