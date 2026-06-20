package com.erdouglass.emdb.media.show;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
      .collect(Collectors.toMap(ShowStatus::toString, Function.identity()));

  private final String status;

  ShowStatus(String status) {
    this.status = status;
  }
  
  public static ShowStatus from(String status) {
    var match = CACHE.get(status);
    return match != null ? match : ShowStatus.valueOf(status);
  }
  
  @Override
  public String toString() {
    return status;
  }
}
