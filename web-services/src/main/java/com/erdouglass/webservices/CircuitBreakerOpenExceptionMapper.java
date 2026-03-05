package com.erdouglass.webservices;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class CircuitBreakerOpenExceptionMapper implements ExceptionMapper<CircuitBreakerOpenException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerOpenExceptionMapper.class);
  
  @Override
  public Response toResponse(CircuitBreakerOpenException exception) {
    LOGGER.error("Service Unavailable", exception);
    return Response.status(Response.Status.SERVICE_UNAVAILABLE)
        .entity(new ErrorResponse("Service is currently offline or unreachable."))
        .build();
  }
  
}
