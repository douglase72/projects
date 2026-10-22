package com.erdouglass.emdb.media.movie.domain.exception;

public class MovieNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public MovieNotFoundException(final String message) {
    super(message);
  }
}
