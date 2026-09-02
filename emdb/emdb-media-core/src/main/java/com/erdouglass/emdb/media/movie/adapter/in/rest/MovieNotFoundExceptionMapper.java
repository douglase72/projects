package com.erdouglass.emdb.media.movie.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.movie.domain.exception.MovieNotFoundException;

@Provider
class MovieNotFoundExceptionMapper implements ExceptionMapper<MovieNotFoundException> {
  private static final Logger LOGGER = Logger.getLogger(MovieNotFoundExceptionMapper.class);
  
  @Override
  public Response toResponse(MovieNotFoundException e) {
    LOGGER.error("Not found", e);
    return Response.status(Response.Status.NOT_FOUND)
        .entity(e.getMessage())
        .build();
  }
}
