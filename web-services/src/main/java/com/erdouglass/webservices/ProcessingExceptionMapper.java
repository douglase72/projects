package com.erdouglass.webservices;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ProcessingExceptionMapper implements ExceptionMapper<ProcessingException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingExceptionMapper.class);
  
  @Override
  public Response toResponse(ProcessingException exception) {
    Throwable cause = exception.getCause();
    String details = (cause != null) ? cause.getMessage() : exception.getMessage();
    LOGGER.error("Downstream Network Failure: {}", details);
    
    return Response.status(Response.Status.SERVICE_UNAVAILABLE)
        .entity(new ErrorResponse("Failed to communicate with the downstream service: " + details))
        .build();
  }
}
