package com.erdouglass.common.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
  private static final Logger LOGGER = Logger.getLogger(IllegalArgumentExceptionMapper.class);
  
  @Override
  public Response toResponse(IllegalArgumentException exception) {
    LOGGER.error("Bad Request", exception);
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(exception.getMessage())
        .build();
  }
}
