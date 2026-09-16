package com.erdouglass.emdb.media.movie.application.port.in;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record UpdateMovieCommand(
    UUID id,
    Long version,
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) {
  
  public UpdateMovieCommand {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(title, "title is required");
  }
}
