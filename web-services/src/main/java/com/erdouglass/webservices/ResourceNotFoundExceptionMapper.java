package com.erdouglass.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceNotFoundExceptionMapper.class);

  @Override
  public Response toResponse(ResourceNotFoundException exception) {
    LOGGER.error("Resource Not Found", exception);
    return Response.status(Response.Status.NOT_FOUND)
        .entity(exception.getMessage())
        .build();
  }

}
