package com.erdouglass.emdb.common;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/// Represents the standardized lifecycle status of a show (Movie or TV Series).
///
/// This enum is used throughout the application, particularly in DTOs, to ensure
/// consistent status representation.
///
/// ## JSON Serialization
/// This enum is customized for Jackson (JSON) serialization and deserialization:
///
/// 1.  **Serialization (`@JsonValue`):** When sent in a response, the enum
///     serializes to its human-readable string (e.g., `"In Production"`).
/// 2.  **Deserialization (`@JsonCreator`):** The `from(String status)` factory
///     method allows clients to send various string formats. It flexibly
///     parses incoming strings (case-insensitive, handles spaces or underscores)
///     to find the correct enum constant.
///
/// @see com.erdouglass.emdb.common.query.MovieDto
/// @see com.erdouglass.emdb.common.command.MovieCreateMessage
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
  
  @JsonCreator
  public static ShowStatus from(String status) {
    return Optional.ofNullable(CACHE.get(status.replace(" ", "_").toUpperCase()))
        .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + status));
  }
  
  @Override
  @JsonValue
  public String toString() {
    return status;
  }
  
}
