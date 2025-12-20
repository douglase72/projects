package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.erdouglass.emdb.common.request.MovieCreateRequest;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieService service;
  
  @POST
  public Response create(@NotNull @Valid MovieCreateRequest request) {
    var movie = service.create(mapper.toMovie(request));
    var location = UriBuilder
        .fromResource(MovieResource.class)
        .path("/{id}")
        .resolveTemplate("id", movie.id())
        .build();
    return Response.created(location).entity(mapper.toMovieDto(movie)).build();
  }

}
