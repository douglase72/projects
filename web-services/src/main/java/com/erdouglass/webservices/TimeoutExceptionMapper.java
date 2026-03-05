package com.erdouglass.webservices;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class TimeoutExceptionMapper implements ExceptionMapper<TimeoutException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutExceptionMapper.class);
  
  @Override
  public Response toResponse(TimeoutException exception) {
    LOGGER.error("Gateway Timeout", exception);
    return Response.status(Response.Status.GATEWAY_TIMEOUT)
        .entity(new ErrorResponse("The server timed out while processing the request."))
        .build();
  }
  
}
