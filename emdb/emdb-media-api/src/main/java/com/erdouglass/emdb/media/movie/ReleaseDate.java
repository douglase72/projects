package com.erdouglass.emdb.media.movie;

import java.time.LocalDate;
import java.util.Objects;

public record ReleaseDate(LocalDate value) {
  public static final LocalDate MIN = LocalDate.of(1874, 1, 1);
  public static final LocalDate MAX = LocalDate.of(2100, 1, 1);

  public ReleaseDate {
    Objects.requireNonNull(value, "release date must not be null");
    if (value.isBefore(MIN) || value.isAfter(MAX)) {
      throw new IllegalArgumentException(
          "release date must be between %s and %s".formatted(MIN, MAX));
    } 
  }
  
  public static ReleaseDate of(LocalDate releaseDate) {
    return new ReleaseDate(releaseDate);
  }
  
  public static ReleaseDate from(String releaseDate) {
    return new ReleaseDate(LocalDate.parse(releaseDate));
  }
}
