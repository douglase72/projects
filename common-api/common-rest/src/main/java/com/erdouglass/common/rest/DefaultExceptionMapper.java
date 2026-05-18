package com.erdouglass.common.rest;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {
  private static final Logger LOGGER = Logger.getLogger(DefaultExceptionMapper.class);
  
  @Override
  public Response toResponse(Throwable throwable) {
    if (throwable instanceof WebApplicationException wae) {
      return wae.getResponse();
    }
    
    Throwable cause = throwable;
    while (cause.getCause() != null) {
      cause = cause.getCause();
    }
    LOGGER.error("Internal Server Error", cause);
    return Response.serverError()
        .entity(new ErrorResponse(throwable.getMessage()))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
