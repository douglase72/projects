package com.erdouglass.emdb.media.person.domain.exception;

public class PersonNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public PersonNotFoundException() {
    super();
  }

  public PersonNotFoundException(final String message) {
    super(message);
  }
  
  public PersonNotFoundException(final Throwable cause) {
    super(cause);
  }
  
  public PersonNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }    
}
