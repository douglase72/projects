package com.erdouglass.emdb.media.person.adapter.in.rest;

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

import com.erdouglass.emdb.media.person.application.port.in.SavePersonUseCase;

@Path("/people")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class PersonResource {
  
  @Inject
  SavePersonUseCase saveUseCase;
  
  @Inject
  CommandMapper mapper;

  @PUT
  @Path("/tmdb/{id}")
  public Response save(
      @NotNull @Positive @PathParam("id") Integer id,
      @NotNull @Valid SavePersonRequest request,
      @Context UriInfo uriInfo) {
    var command = mapper.toSavePersonCommand(id, request);
    saveUseCase.save(command);
    return Response.ok().build();
  }
}
