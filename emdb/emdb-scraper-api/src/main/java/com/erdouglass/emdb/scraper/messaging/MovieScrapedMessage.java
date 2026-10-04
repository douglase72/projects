package com.erdouglass.emdb.scraper.messaging;

import java.math.BigDecimal;
import java.util.Objects;

import com.erdouglass.emdb.shared.kernel.TmdbId;

public record MovieScrapedMessage(
    TmdbId tmdbId,
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) {

  public MovieScrapedMessage {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
  }   
}
