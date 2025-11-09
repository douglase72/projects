package com.erdouglass.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

  @Override
  public Response toResponse(final WebApplicationException exception) {
    LOGGER.error("Error", exception);
    return Response.status(exception.getResponse().getStatus()).build();
  }

}