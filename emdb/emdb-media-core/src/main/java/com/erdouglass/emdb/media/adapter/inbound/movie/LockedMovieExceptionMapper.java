package com.erdouglass.emdb.media.adapter.inbound.movie;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.domain.exception.LockedMovieException;

@Provider
class LockedMovieExceptionMapper implements ExceptionMapper<LockedMovieException> {
  private static final Logger LOGGER = Logger.getLogger(LockedMovieExceptionMapper.class);
  
  @Override
  public Response toResponse(LockedMovieException e) {
    LOGGER.warn("Locked", e);
    return Response.status(423)
        .entity(e.getMessage())
        .build();
  }
}
