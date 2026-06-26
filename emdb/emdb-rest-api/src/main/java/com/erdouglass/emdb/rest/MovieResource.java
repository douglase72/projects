package com.erdouglass.emdb.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.media.movie.MovieDto;
import com.erdouglass.emdb.media.movie.MovieService;
import com.erdouglass.emdb.media.movie.SaveMovie;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {

  @Inject
  MovieService service;
  
  @POST
  public MovieDto save(@NotNull @Valid SaveMovie command) {
    return service.save(command);
  }  
}
