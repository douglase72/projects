package com.erdouglass.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintViolationExceptionMapper.class);
  
  @Override
  public Response toResponse(ConstraintViolationException exception) {
    LOGGER.error("Constraint Violation", exception);
    return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
  }

}
