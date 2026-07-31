package com.erdouglass.emdb.ingest.domain.exception;

public class IngestNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public IngestNotFoundException(final String message) {
    super(message);
  }
  
  public IngestNotFoundException(final Throwable cause) {
    super(cause);
  }
  
  public IngestNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }  
}
