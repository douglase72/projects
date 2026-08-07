package com.erdouglass.emdb.media.domain.exception;

public class StaleMovieException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public StaleMovieException() {
    super();
  }

  public StaleMovieException(final String message) {
    super(message);
  }
  
  public StaleMovieException(final Throwable cause) {
    super(cause);
  }
  
  public StaleMovieException(final String message, final Throwable cause) {
    super(message, cause);
  }    
}
