package com.erdouglass.common.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class StaleVersionExceptionMapper implements ExceptionMapper<StaleVersionException> {
  private static final Logger LOGGER = Logger.getLogger(StaleVersionExceptionMapper.class);
  
  @Override
  public Response toResponse(StaleVersionException e) {
    LOGGER.error("Stale Version", e);
    return Response.status(Response.Status.CONFLICT)
        .entity(e.getMessage())
        .build();
  }
}
