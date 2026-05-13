package com.erdouglass.emdb.media.movie;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com.erdouglass.emdb.common.api.command.SaveMovie;
import com.erdouglass.emdb.common.api.query.MovieDto;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  MovieService service;
  
  @POST
  public Response save(@NotNull @Valid SaveMovie command) {
    var movie = new Movie();
    movie.setTitle(command.title());
    movie.setReleaseDate(command.releaseDate());
    var savedMovie = service.save(movie);
    return Response.status(Status.CREATED)
        .entity(new MovieDto(savedMovie.getId(), savedMovie.getTitle(), savedMovie.getReleaseDate()))
        .build();
  }
}
