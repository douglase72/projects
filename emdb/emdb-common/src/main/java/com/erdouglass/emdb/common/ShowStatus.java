package com.erdouglass.emdb.common;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/// Represents the production or release life-cycle status of a show or movie.
public enum ShowStatus {
  CANCELED("Canceled"),
  ENDED("Ended"),
  IN_PRODUCTION("In Production"),
  PILOT("Pilot"),
  PLANNED("Planned"),
  POST_PRODUCTION("Post Production"),
  RELEASED("Released"),
  RETURNING_SERIES("Returning Series"),
  RUMORED("Rumored");
    
  private static final Map<String, ShowStatus> CACHE = Stream.of(values())
      .collect(Collectors.toMap(ShowStatus::name, Function.identity()));

  private final String status;

  ShowStatus(String status) {
    this.status = status;
  }
  
  /// Resolves a `ShowStatus` from its string representation.
  ///
  /// Converts spaces to underscores and ignores case when mapping 
  /// the input string to an enum constant.
  ///
  /// @param status the raw string value to parse
  /// @return the corresponding [ShowStatus]
  /// @throws IllegalArgumentException if the string does not map to a valid statu
  @JsonCreator
  public static ShowStatus from(String status) {
    return Optional.ofNullable(CACHE.get(status
        .replace(Configuration.SPACE, Configuration.UNDERSCORE).toUpperCase()))
        .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + status));
  }
  
  @Override
  @JsonValue
  public String toString() {
    return status;
  }
  
}
