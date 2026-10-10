package com.erdouglass.emdb.scraper.domain.event;

import java.util.Objects;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.scraper.domain.model.MovieDetails;
import com.erdouglass.emdb.shared.kernel.CorrelationId;

public record MovieExtracted(
    MessageId id, CorrelationId correlationId, MovieDetails details) implements MovieEvent {

  public MovieExtracted {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(correlationId, "correlationId must not be null");
    Objects.requireNonNull(details, "movie details must not be null");
  }
  
  public static MovieExtracted of(MessageId id, CorrelationId correlationId, MovieDetails details) {
    return new MovieExtracted(id, correlationId, details);
  }
}
