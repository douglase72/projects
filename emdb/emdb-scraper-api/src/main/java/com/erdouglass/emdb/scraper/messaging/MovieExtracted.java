package com.erdouglass.emdb.scraper.messaging;

import java.math.BigDecimal;
import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.shared.kernel.PublicId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record MovieExtracted(
    MessageId messageId,
    PublicId correlationId,
    TmdbId tmdbId,
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) {
  
  public MovieExtracted {
    Objects.requireNonNull(messageId, "messageId must not be null");
    Objects.requireNonNull(correlationId, "correlationId must not be null");
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
  }   
}
