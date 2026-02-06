package com.erdouglass.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.data.exceptions.EntityExistsException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EntityExistsExceptionMapper implements ExceptionMapper<EntityExistsException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(EntityExistsExceptionMapper.class);
  
  /**
   * Translates an {@link EntityExistsException} into an HTTP 409 Conflict response.
   * <p>
   * The exception is logged, and the response body is populated with the exception's
   * message to provide context to the client about the conflict.
   *
   * @param exception the {@code EntityExistsException} that was thrown.
   * @return a {@link Response} object representing a 409 Conflict, containing the
   * error message.
   */
  @Override
  public Response toResponse(EntityExistsException exception) {
    LOGGER.error("Entity Already Exists", exception);
    return Response.status(Response.Status.CONFLICT).entity(exception.getMessage()).build();
  }

}
