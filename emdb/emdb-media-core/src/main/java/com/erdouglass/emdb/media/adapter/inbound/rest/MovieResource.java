package com.erdouglass.emdb.media.adapter.inbound.rest;

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

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieUseCase;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MovieResource {

  @Inject
  SaveMovieUseCase saveUseCase;
  
  @POST
  public Response save(@NotNull @Valid SaveMovieCommand command, @Context UriInfo uriInfo) {
    var result = saveUseCase.save(command);
    return switch (result.status()) {
      case CREATED -> Response
          .created(uriInfo.getAbsolutePathBuilder().path(result.id()).build())
          .entity(result)
          .build();
      case UPDATED -> Response.ok(result).build();
    };
  }
}
