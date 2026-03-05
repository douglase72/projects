package com.erdouglass.webservices;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.faulttolerance.api.RateLimitException;

@Provider
public class RateLimitExceptionMapper implements ExceptionMapper<RateLimitException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitExceptionMapper.class);

  @Override
  public Response toResponse(RateLimitException exception) {
    LOGGER.error("Rate Limit Exceeded", exception);
    return Response.status(ResponseStatus.TOO_MANY_REQUESTS)
        .entity(new ErrorResponse("Rate limit exceeded."))
        .header("Retry-After", "1")
        .build();
  }
}
