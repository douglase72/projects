package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.media.dto.SaveResponse;
import com.erdouglass.emdb.media.dto.UpdateResponse;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.movie.application.port.in.DeleteMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieUseCase;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MovieResource {
  
  @Inject
  SaveMovieUseCase saveUseCase;
  
  @Inject
  UpdateMovieUseCase updateUseCase;
  
  @Inject
  DeleteMovieUseCase deleteUseCase;
  
  @Inject
  CommandMapper mapper;

  @PUT
  @Path("/tmdb/{id}")
  public Response save(
      @NotNull @Positive @PathParam("id") Integer id,
      @NotNull @Valid SaveMovieRequest request,
      @Context UriInfo uriInfo) {
    var command = mapper.toSaveMovieCommand(id, request);
    var result = saveUseCase.save(command);
    return switch (result.status()) {
      case CREATED -> Response.created(uriInfo.getBaseUriBuilder()
          .path(MovieResource.class)
          .path(result.id().value().toString())
          .build())
        .entity(SaveResponse.of(result.id().value(), result.status().toString()))
        .build();
      case UPDATED, UNCHANGED -> Response
        .ok(SaveResponse.of(result.id().value(), result.status().toString())).build();
    };    
  }
  
  @PUT
  @Path("/{id}")
  public UpdateResponse update(
      @NotNull @PathParam("id") UUID id, 
      @NotNull @Valid UpdateMovieRequest request) {
    var command = mapper.toUpdateMovieCommand(PublicId.of(id), request);
    var result = updateUseCase.update(command);
    return switch (result.status()) {
      case UPDATED, UNCHANGED -> UpdateResponse.of(id, result.version().value(), result.status().toString());
    };
  } 
  
  @DELETE
  @Path("/{id}")
  public Response delete(@NotNull @PathParam("id") UUID id) {
    deleteUseCase.deleteById(PublicId.of(id));
    return Response.noContent().build();
  }
}
