package com.erdouglass.emdb.media.movie.application.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record MovieView(
    UUID id,
    Long version,
    String title,
    LocalDate releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) { 
  
  public MovieView {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(title, "title is required");
  }
}
