package com.erdouglass.emdb.media.domain.exception;

public class MovieNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public MovieNotFoundException() {
    super();
  }

  public MovieNotFoundException(final String message) {
    super(message);
  }
  
  public MovieNotFoundException(final Throwable cause) {
    super(cause);
  }
  
  public MovieNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }    
}
