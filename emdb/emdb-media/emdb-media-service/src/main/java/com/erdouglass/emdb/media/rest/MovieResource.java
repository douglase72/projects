package com.erdouglass.emdb.media.rest;

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

import com.erdouglass.emdb.media.domain.MovieService;
import com.erdouglass.emdb.media.movie.MovieResponse;
import com.erdouglass.emdb.media.movie.SaveMovie;

/// JAX-RS resource exposing the movie collection over HTTP. Translates
/// [SaveMovie] commands into [MovieService] calls and shapes the response
/// envelope.
@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  MovieService service;
  
  @Context
  UriInfo uriInfo;

  /// Creates or updates a movie from the given [SaveMovie] command.
  ///
  /// The underlying service performs an upsert keyed by TMDB identifier, so
  /// this endpoint is idempotent with respect to that identifier.
  ///
  /// @param command the validated save-movie payload
  /// @return `201 Created` with a `Location` header pointing to the canonical
  ///         resource URI and a [MovieResponse] body  
  @POST
  public Response save(@NotNull @Valid final SaveMovie command) {
    var movie = service.save(command);
    var location = uriInfo.getAbsolutePathBuilder()
        .path(String.valueOf(movie.id()))
        .build();       
    return Response.created(location).entity(movie).build();
  }  
}
