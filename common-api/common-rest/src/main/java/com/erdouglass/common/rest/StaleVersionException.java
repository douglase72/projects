package com.erdouglass.common.rest;

public class StaleVersionException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public StaleVersionException() {
    super();
  }

  public StaleVersionException(final String message) {
    super(message);
  }
  
  public StaleVersionException(final Throwable cause) {
    super(cause);
  }
  
  public StaleVersionException(final String message, final Throwable cause) {
    super(message, cause);
  }  
}
