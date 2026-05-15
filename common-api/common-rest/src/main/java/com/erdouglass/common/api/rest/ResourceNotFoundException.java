package com.erdouglass.common.api.rest;

public class ResourceNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public ResourceNotFoundException(String message) {
    super(message);
  }
  
  public ResourceNotFoundException(Throwable cause) {
    super(cause);
  }
  
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
