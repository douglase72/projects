package com.erdouglass.emdb.media.domain.exception;

public class LockedMovieException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public LockedMovieException() {
    super();
  }

  public LockedMovieException(final String message) {
    super(message);
  }
}
