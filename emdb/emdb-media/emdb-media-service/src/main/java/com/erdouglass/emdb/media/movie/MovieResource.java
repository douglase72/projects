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

import com.erdouglass.emdb.media.command.SaveMovie;

/// JAX-RS resource exposing the movie collection over HTTP. Translates
/// [SaveMovie] commands into [MovieService] calls and shapes the response
/// envelope.
@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MovieResource {
  
  @Inject
  MovieService service;
  
  /// Creates or updates a movie from the request body and returns the persisted
  /// representation.
  ///
  /// @param command the validated movie payload
  /// @return a `200 OK` response carrying the [MovieResponse]
  @POST
  public Response save(@NotNull @Valid final SaveMovie command) {
    var movie = service.save(command);
    return Response.ok()
        .entity(movie)
        .build();
  }
}
