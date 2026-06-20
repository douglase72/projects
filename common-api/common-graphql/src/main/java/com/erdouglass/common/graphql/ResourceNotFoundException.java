package com.erdouglass.common.graphql;

import io.smallrye.graphql.api.ErrorCode;

@ErrorCode("not-found")
public class ResourceNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ResourceNotFoundException(final String message) {
    super(message);
  }
  
  public ResourceNotFoundException(final Throwable cause) {
    super(cause);
  }
  
  public ResourceNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
