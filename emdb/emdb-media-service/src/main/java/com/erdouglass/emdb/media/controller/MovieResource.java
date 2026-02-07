package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;
import com.erdouglass.webservices.ResourceNotFoundException;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieService service;
  
  @POST
  public MovieDto save(@NotNull @Valid SaveMovie command) {
    var movie = service.save(command);
    return mapper.toMovieDto(movie);
  } 
  
  @GET
  @Path("/{id}")
  public MovieDto findById(
      @PathParam("id") @NotNull @Positive Long id,
      @QueryParam(Configuration.APPEND) String append) {
    var movie = service.findById(id, append)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    return mapper.toMovieDto(movie);
  }  

}
