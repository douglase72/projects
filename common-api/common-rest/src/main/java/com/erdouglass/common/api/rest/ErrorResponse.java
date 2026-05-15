package com.erdouglass.common.api.rest;

import java.util.List;

public record ErrorResponse(List<String> errors) {
  
  public ErrorResponse(String error) {
    this(List.of(error));
  }
}