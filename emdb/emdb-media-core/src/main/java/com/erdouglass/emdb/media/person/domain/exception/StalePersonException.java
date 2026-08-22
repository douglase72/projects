package com.erdouglass.emdb.media.person.domain.exception;

public class StalePersonException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public StalePersonException() {
    super();
  }

  public StalePersonException(final String message) {
    super(message);
  }
  
  public StalePersonException(final Throwable cause) {
    super(cause);
  }
  
  public StalePersonException(final String message, final Throwable cause) {
    super(message, cause);
  }    
}
