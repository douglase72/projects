package com.erdouglass.emdb.media.movie.adapter.in.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MovieResource {

  @Inject
  SaveMovieUseCase saveUseCase;
  
  @PUT
  @Path("/tmdb/{id}")
  public Response save(
      @NotNull @Positive @PathParam("id") Integer id,
      @NotNull @Valid SaveMovieRequest request,
      @Context UriInfo uriInfo) {
    var command = CommandMapper.toSaveMovieCommand(id, request);
    var result = saveUseCase.save(command);
    return switch (result.status()) {
      case CREATED -> Response
        .created(uriInfo.getBaseUriBuilder()
            .path(MovieResource.class)
            .path(result.id().value().toString())
            .build())
        .entity(MovieResponse.of(result.id().value(), result.version().value(), result.status().toString()))
        .build();
      case UPDATED, UNCHANGED -> Response
        .ok(MovieResponse.of(result.id().value(), result.version().value(), result.status().toString()))
        .build();
    };
  }
}
