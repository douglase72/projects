package com.erdouglass.emdb.media.person.domain.exception;

public class LockedPersonException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public LockedPersonException() {
    super();
  }

  public LockedPersonException(final String message) {
    super(message);
  }
}
