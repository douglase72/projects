package com.erdouglass.emdb.media.core.credit;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CreditType {
  CAST("cast"), 
  CREW("crew");
  
  private static final Map<String, CreditType> CACHE = Stream.of(values())
      .collect(Collectors.toMap(CreditType::name, Function.identity()));
  
  private final String type;
  
  CreditType(String type) {
    this.type = type;
  }
  
  /// Resolves a constant from its name, case-insensitively; serves as the
  /// Jackson factory for deserialization. Because the wire form is the
  /// lowercased name, values produced by [#toString()] round-trip cleanly.
  ///
  /// @param type the credit type name (e.g. `"cast"`, `"CREW"`)
  /// @return the matching constant
  /// @throws IllegalArgumentException if no constant matches
  public static CreditType from(String type) {
    return Optional.ofNullable(CACHE.get(type.toUpperCase()))
        .orElseThrow(() -> new IllegalArgumentException("Invalid credit type: " + type));
  }
   
  @Override
  public String toString() {
    return type;
  }
}
