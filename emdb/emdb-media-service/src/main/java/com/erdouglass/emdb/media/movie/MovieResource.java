package com.erdouglass.emdb.media.movie;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.common.api.command.SaveMovie;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieService service;
  
  @Context
  UriInfo uriInfo;
  
  @POST
  public Response save(@NotNull @Valid final SaveMovie command) {
    var savedMovie = service.save(mapper.toMovie(command));
    var location = uriInfo.getAbsolutePathBuilder()
        .path(String.valueOf(savedMovie.getId()))
        .build();    
    return Response.created(location)
        .entity(mapper.toMovieDto(savedMovie))
        .build();
  }
}
