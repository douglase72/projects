package com.erdouglass.webservices;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
  private static final Logger LOGGER = Logger.getLogger(ConstraintViolationExceptionMapper.class);
  
  @Override
  public Response toResponse(ConstraintViolationException e) {
    LOGGER.error("Validation failed", e);
    return Response.status(Response.Status.BAD_REQUEST)
        .build();
  }

}
