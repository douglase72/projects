package com.erdouglass.emdb.media.adapter.inbound.movie;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.domain.exception.StaleMovieException;

@Provider
class StaleMovieExceptionMapper implements ExceptionMapper<StaleMovieException> {
  private static final Logger LOGGER = Logger.getLogger(StaleMovieExceptionMapper.class);
  
  @Override
  public Response toResponse(StaleMovieException e) {
    LOGGER.error("Conflict", e);
    return Response.status(Response.Status.CONFLICT).build();
  }
}
