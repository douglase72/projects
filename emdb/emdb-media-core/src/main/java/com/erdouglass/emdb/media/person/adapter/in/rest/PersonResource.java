package com.erdouglass.emdb.media.person.adapter.in.rest;

import java.util.UUID;

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
import com.erdouglass.emdb.media.person.application.port.in.UpdatePersonUseCase;

@Path("/people")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class PersonResource {

  @Inject
  SavePersonUseCase saveUseCase;
  
  @Inject
  UpdatePersonUseCase updateUseCase;
  
  @PUT
  @Path("/tmdb/{id}")
  public Response save(
      @NotNull @Positive @PathParam("id") Integer id,
      @NotNull @Valid SavePersonRequest request,
      @Context UriInfo uriInfo) {
    var command = CommandMapper.toSavePersonCommand(id, request);
    var result = saveUseCase.save(command);
    return switch (result.status()) {
      case CREATED -> Response
        .created(uriInfo.getBaseUriBuilder().path(PersonResource.class).path(result.id().value().toString()).build())
        .entity(SavePersonResponse.of(result.id().value(), result.status().toString()))
        .build();
      case UPDATED, UNCHANGED -> Response
        .ok(SavePersonResponse.of(result.id().value(), result.status().toString()))
        .build();
    };
  }
  
  @PUT
  @Path("/{id}")
  public UpdatePersonResponse update(
      @NotNull @PathParam("id") UUID id, 
      @NotNull @Valid UpdatePersonRequest request) {
    var command = CommandMapper.toUpdatePersonCommand(id, request);
    var result = updateUseCase.update(command);
    return switch (result.status()) {
      case UPDATED, UNCHANGED -> UpdatePersonResponse.of(id, result.version().value(), result.status().toString());
    };
  }
}
