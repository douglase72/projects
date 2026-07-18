package com.erdouglass.emdb.media.domain.shared;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record SourceId(Source source, String id) {

  public SourceId {
    if (source == null) {
      throw new IllegalArgumentException("source must not be null");
    }
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
  }
  
  public enum Source {
    IMDB("imdb"),
    OMDB("omdb"),
    TMDB("tmdb"),
    TRAKT("trakt");
    
    private static final Map<String, Source> CACHE = Stream.of(values())
        .collect(Collectors.toMap(Source::toString, Function.identity()));
    
    private final String source;
    
    Source(String source) {
      this.source = source;
    }
    
    public static Source from(String source) {
      var value = CACHE.get(source);
      return value != null ? value : Source.valueOf(source);
    }
    
    @Override
    public String toString() {
      return source;
    }
  }
}
