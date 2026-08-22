package com.erdouglass.emdb.media.movie.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.movie.domain.exception.StaleMovieException;

@Provider
class StaleMovieExceptionMapper implements ExceptionMapper<StaleMovieException> {
  private static final Logger LOGGER = Logger.getLogger(StaleMovieExceptionMapper.class);
  
  @Override
  public Response toResponse(StaleMovieException e) {
    LOGGER.error("Conflict", e);
    return Response.status(Response.Status.CONFLICT).build();
  }
}
