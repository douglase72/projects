package com.erdouglass.emdb.media;

import java.time.LocalDate;

public record ReleaseDate(LocalDate value) {
  public static final LocalDate MIN = LocalDate.of(1888, 1, 1);
  public static final LocalDate MAX = LocalDate.of(2100, 1, 1);

  public ReleaseDate {
    if (value == null) {
      throw new IllegalArgumentException("release date must not be null");
    }
    if (value.isBefore(MIN) || value.isAfter(MAX)) {
      throw new IllegalArgumentException(
          "release date must be between %s and %s".formatted(MIN, MAX));
    } 
  }
  
  public static ReleaseDate of(LocalDate releaseDate) {
    return new ReleaseDate(releaseDate);
  }
  
  @Override
  public String toString() {
    return value.toString();
  }
}
