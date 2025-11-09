package com.erdouglass.webservices;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status.Family;

public enum ResponseStatus implements Response.StatusType {
  MULTI_STATUS(207, "Multi-Status");
  
  private static final Map<Integer, ResponseStatus> CACHE = Stream.of(values())
      .collect(Collectors.toMap(ResponseStatus::getStatusCode, Function.identity()));
  
  private final int statusCode;
  private final String reason;
  
  ResponseStatus(int statusCode, String reason) {
    this.statusCode = statusCode;
    this.reason = reason;
  }
  
  public static ResponseStatus from(int statusCode) {
    return Optional.ofNullable(CACHE.get(statusCode))
        .orElseThrow(() -> new IllegalArgumentException("Invalid status code: " + statusCode));
  }
  
  @Override
  public int getStatusCode() {
    return statusCode;
  }

  @Override
  public Family getFamily() {
    return Response.Status.Family.familyOf(statusCode);
  }

  @Override
  public String getReasonPhrase() {
    return reason;
  }
  
}
