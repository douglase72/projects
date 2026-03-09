package com.erdouglass.webservices;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import io.grpc.StatusRuntimeException;

@Provider
public class StatusRuntimeExceptionMapper implements ExceptionMapper<StatusRuntimeException> {
  private static final Logger LOGGER = Logger.getLogger(StatusRuntimeExceptionMapper.class);

  @Override
  public Response toResponse(StatusRuntimeException exception) {
    var status = exception.getStatus();
    Response.Status httpStatus = switch (status.getCode()) {
      case INVALID_ARGUMENT -> Response.Status.BAD_REQUEST;
      case NOT_FOUND -> Response.Status.NOT_FOUND;
      case ALREADY_EXISTS -> Response.Status.CONFLICT;
      case UNAUTHENTICATED -> Response.Status.UNAUTHORIZED;
      case PERMISSION_DENIED -> Response.Status.FORBIDDEN;
      case UNAVAILABLE -> Response.Status.SERVICE_UNAVAILABLE;
      default -> Response.Status.INTERNAL_SERVER_ERROR;
    };
    LOGGER.error(httpStatus, exception);
    return Response.status(httpStatus)
        .entity(status.getDescription()) 
        .build();    
  }

}
