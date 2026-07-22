package com.erdouglass.emdb.media;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// External identity of a catalog item: which provider ([Source]) knows it,
/// and by what id.
///
/// Lives in `domain.shared` as the context's shared kernel — the one identity
/// vocabulary movies, people, and series all use. Doubles as the natural key
/// for idempotent ingestion (see [MovieRepository#save]); uniqueness
/// across aggregates is a cross-aggregate rule no single aggregate can
/// enforce, so its ultimate guard is the database constraint.
public record SourceId(Source source, String id) {

  public SourceId {
    if (source == null) {
      throw new IllegalArgumentException("source must not be null");
    }
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
  }
  
  public static SourceId of(Source source, String id) {
    return new SourceId(source, id);
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
