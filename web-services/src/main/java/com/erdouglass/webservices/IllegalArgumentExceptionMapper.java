package com.erdouglass.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(IllegalArgumentExceptionMapper.class);

  @Override
  public Response toResponse(IllegalArgumentException exception) {
    LOGGER.error("Bad Request", exception);
    return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
  }

}
