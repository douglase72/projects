package com.erdouglass.emdb.media.domain.movie;

import java.util.Objects;

import com.erdouglass.emdb.media.MediaType;
import com.erdouglass.emdb.media.domain.exception.MovieNotFoundException;

public record MoviePublicId(Long value) {
  
  public MoviePublicId {
    if (value == null || value < 1) {
      throw new IllegalArgumentException("invalid public id");
    }
  }
  
  public static MoviePublicId of(Long value) {
    return new MoviePublicId(value);
  }
  
  public static MoviePublicId from(String id) {
    int i = Objects.requireNonNull(id, "id must not be null").indexOf('_');
    if (i < 0) throw new IllegalArgumentException("invalid id: " + id);
    if (MediaType.from(id.substring(0, i)) != MediaType.MOVIE) {
      throw new MovieNotFoundException(id);
    }
    return new MoviePublicId(Long.parseLong(id.substring(i + 1)));
  }

  @Override
  public String toString() {
    return "mv_" + value;
  }
}
