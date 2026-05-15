package com.erdouglass.common.api.rest;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class PersistenceConstraintViolationExceptionMapper 
    implements ExceptionMapper<ConstraintViolationException> {
  private static final Logger LOGGER = Logger.getLogger(PersistenceConstraintViolationExceptionMapper.class);
  
  @Override
  public Response toResponse(ConstraintViolationException exception) {
    LOGGER.error("Persistence Coponflict", exception);
    return Response.status(Response.Status.CONFLICT)
        .entity(new ErrorResponse(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
