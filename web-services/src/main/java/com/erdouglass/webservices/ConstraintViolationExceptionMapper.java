package com.erdouglass.webservices;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintViolationExceptionMapper.class);
  
  @Override
  public Response toResponse(ConstraintViolationException exception) {
    LOGGER.error("Constraint Violation", exception);
    var errors = exception.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(new ErrorResponse(errors))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

}
