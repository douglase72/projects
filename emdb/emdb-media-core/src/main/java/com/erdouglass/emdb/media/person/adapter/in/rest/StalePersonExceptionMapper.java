package com.erdouglass.emdb.media.person.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.movie.domain.exception.StaleMovieException;

@Provider
class StalePersonExceptionMapper implements ExceptionMapper<StaleMovieException> {
  private static final Logger LOGGER = Logger.getLogger(StalePersonExceptionMapper.class);
  
  @Override
  public Response toResponse(StaleMovieException e) {
    LOGGER.error("Conflict", e);
    return Response.status(Response.Status.CONFLICT).build();
  }
}
