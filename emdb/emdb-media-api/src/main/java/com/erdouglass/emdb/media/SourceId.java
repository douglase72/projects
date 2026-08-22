package com.erdouglass.emdb.media;

import java.util.Locale;
import java.util.Objects;

public record SourceId(Source provider, String id) {
  
  public SourceId {
    Objects.requireNonNull(provider, "provider must not be null");
    Objects.requireNonNull(id, "id must not be null");
  }
  
  public static SourceId of(Source provider, String id) {
    return new SourceId(provider, id);
  }
  
  public enum Source {
    IMDB("IMDB"),
    OMDB("OMDB"),
    TMDB("TMDB"),
    TRAKT("Trakt");
    
    public static final int MAX_LENGTH = 16;
    
    private final String source;
    
    Source(String source) {
      this.source = source;
    }
    
    public static Source from(String source) {
      return valueOf(Objects.requireNonNull(source, "value").toUpperCase(Locale.ROOT));
    }
    
    @Override
    public String toString() {
      return source;
    }
  }
}
