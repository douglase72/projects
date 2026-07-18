package com.erdouglass.emdb.media.domain.movie;

import java.time.LocalDate;

import com.erdouglass.emdb.media.MediaConstants;

public record ReleaseDate(LocalDate value) {

  public ReleaseDate {
    if (value == null) {
      throw new IllegalArgumentException("release date must not be null");
    }
    if (value.isBefore(LocalDate.parse(MediaConstants.MOVIE_MIN_DATE))) {
      throw new IllegalArgumentException(
          "release date must not be before %s".formatted(MediaConstants.MOVIE_MIN_DATE));
    }
    if (value.isAfter(LocalDate.parse(MediaConstants.MAX_DATE))) {
      throw new IllegalArgumentException(
          "release date must not be after %s".formatted(MediaConstants.MAX_DATE));
    }    
  }
  
  @Override
  public String toString() {
    return value.toString();
  }
}
