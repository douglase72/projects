package com.erdouglass.webservices;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceNotFoundExceptionMapper.class);

  @Override
  public Response toResponse(ResourceNotFoundException exception) {
    LOGGER.error("Resource Not Found", exception);
    return Response.status(Response.Status.NOT_FOUND)
        .entity(new ErrorResponse(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

}
