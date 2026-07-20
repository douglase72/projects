package com.erdouglass.emdb.media;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// Discriminator for public identifiers: the short token prefixing every
/// [PublicId] and naming the media kind on the wire.
public enum MediaType {
  MOVIE("mv"), 
  PERSON("pr"),
  SERIES("sr");
  
  private static final Map<String, MediaType> CACHE = Stream.of(values())
      .collect(Collectors.toMap(MediaType::toString, Function.identity()));
  
  private final String type;
  
  MediaType(String type) {
    this.type = type;
  }
  
  /// Resolves either spelling — wire token (`"mv"`) or constant name
  /// (`"MOVIE"`) — trying the token first.
  ///
  /// @throws IllegalArgumentException if it is neither
  public static MediaType from(String type) {
    var match = CACHE.get(type);
    return match != null ? match : MediaType.valueOf(type);
  }
  
  @Override
  public String toString() {
    return type;
  }
}
