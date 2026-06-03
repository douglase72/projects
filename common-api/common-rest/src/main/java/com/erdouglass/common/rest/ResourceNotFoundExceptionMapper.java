package com.erdouglass.common.rest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {
  private static final Logger LOGGER = Logger.getLogger(ResourceNotFoundExceptionMapper.class);

  @Override
  public Response toResponse(ResourceNotFoundException exception) {
    LOGGER.error("Resource Not Found", exception);
    return Response.status(Response.Status.NOT_FOUND)
        .entity(new ErrorResponse(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
