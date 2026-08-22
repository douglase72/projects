package com.erdouglass.emdb.media.person.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.person.domain.exception.StalePersonException;

@Provider
class StalePersonExceptionMapper implements ExceptionMapper<StalePersonException> {
  private static final Logger LOGGER = Logger.getLogger(StalePersonExceptionMapper.class);
  
  @Override
  public Response toResponse(StalePersonException e) {
    LOGGER.error("Conflict", e);
    return Response.status(Response.Status.CONFLICT)
        .entity(e.getMessage())
        .build();
  }
}

