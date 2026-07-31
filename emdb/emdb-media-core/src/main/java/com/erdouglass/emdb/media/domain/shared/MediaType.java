package com.erdouglass.emdb.media.domain.shared;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// Discriminator for public identifiers: the short token prefixing every
/// [PublicId] and naming the media kind on the wire.
public enum MediaType {
  MOVIE("mv", "movie"), 
  PERSON("pr", "person"),
  SERIES("sr", "series");
  
  private static final Map<String, MediaType> CACHE = Stream.of(values())
      .collect(Collectors.toMap(MediaType::prefix, Function.identity()));
  
  private final String prefix;
  private final String type;
  
  MediaType(String prefix, String type) {
    this.prefix = prefix;
    this.type = type;
  }
  
  public static MediaType from(String prefix) {
    var match = CACHE.get(prefix);
    return match != null ? match : MediaType.valueOf(prefix);
  }
  
  public String prefix() {
    return prefix;
  }
  
  @Override
  public String toString() {
    return type;
  }
}
