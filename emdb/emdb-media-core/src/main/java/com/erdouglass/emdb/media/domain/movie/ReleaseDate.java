package com.erdouglass.emdb.media.domain.movie;

import java.time.LocalDate;
import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.common.util.DateTimeFactory;

public record ReleaseDate(DateTime value) {
  public static final DateTime MIN = DateTimeFactory.from(1874, 1, 1);
  public static final DateTime MAX = DateTimeFactory.from(2100, 1, 1);

  public ReleaseDate {
    Objects.requireNonNull(value, "release date must not be null");
    if (value.isBefore(MIN) || value.isAfter(MAX)) {
      throw new IllegalArgumentException(
          "release date must be between %s and %s".formatted(MIN, MAX));
    } 
  }
  
  public static ReleaseDate of(DateTime releaseDate) {
    return new ReleaseDate(releaseDate);
  }
  
  public static ReleaseDate from(LocalDate releaseDate) {
    return new ReleaseDate(DateTimeFactory.from(releaseDate));
  }
  
  public static ReleaseDate from(String releaseDate) {
    return new ReleaseDate(DateTimeFactory.from(releaseDate));
  }
  
  public LocalDate toLocalDate() {
    return value.toLocalDate();
  }
}
