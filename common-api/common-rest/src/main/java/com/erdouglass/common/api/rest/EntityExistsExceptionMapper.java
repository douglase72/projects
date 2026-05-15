package com.erdouglass.common.api.rest;

import jakarta.data.exceptions.EntityExistsException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class EntityExistsExceptionMapper implements ExceptionMapper<EntityExistsException> {

  private static final Logger LOGGER = Logger.getLogger(EntityExistsExceptionMapper.class);

  @Override
  public Response toResponse(EntityExistsException exception) {
    LOGGER.warn("Entity Already Exists", exception);
    return Response.status(Response.Status.CONFLICT)
        .entity(new ErrorResponse(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
