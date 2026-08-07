package com.erdouglass.emdb.media.domain.movie;

import java.util.Objects;
import java.util.regex.Pattern;

public record MoviePublicId(String value) {
  private static final String PREFIX = "mv_";
  private static final Pattern SHAPE = Pattern.compile("^mv_[1-9]\\d*$");
  
  public MoviePublicId {
    Objects.requireNonNull(value, "movie id must not be null");
    if (!SHAPE.matcher(value).matches()) {
      throw new IllegalArgumentException("movie id must match mv_<n>, got: " + value);
    }    
  }
  
  public static MoviePublicId of(String id) {
    return new MoviePublicId(id);
  }
  
  public static MoviePublicId from(long id) {
    if (id < 1) {
      throw new IllegalArgumentException("id must be positive");
    }
    return new MoviePublicId(PREFIX + id);
  }
  
  public Long toLong() {
    return Long.parseLong(value.substring(PREFIX.length()));
  }
}
