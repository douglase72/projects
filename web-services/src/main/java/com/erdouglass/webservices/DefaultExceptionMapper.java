package com.erdouglass.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionMapper.class);
  
  @Override
  public Response toResponse(Throwable throwable) {
    Throwable cause = throwable;
    while (cause.getCause() != null) {
      cause = cause.getCause();
    }
    
    if (cause instanceof ConstraintViolationException) {
      LOGGER.error("Constraint Violation", cause);
      return Response.status(Response.Status.BAD_REQUEST).entity(cause.getMessage()).build();
    }
    LOGGER.error("Internal Server Error", cause);
    return Response.serverError().entity(cause.getMessage()).build();
  }
  
}
