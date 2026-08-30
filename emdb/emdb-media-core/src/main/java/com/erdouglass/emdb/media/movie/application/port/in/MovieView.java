package com.erdouglass.emdb.media.movie.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovieView(
    Long id,
    Long version,
    String title,
    LocalDate releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) { 
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id
        + ", version=" + version
        + ", title=" + title
        + ", releaseDate=" + releaseDate
        + ", score=" + score
        + "]";
  }
}
