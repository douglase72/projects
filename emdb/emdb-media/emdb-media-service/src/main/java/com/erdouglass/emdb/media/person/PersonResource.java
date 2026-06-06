package com.erdouglass.emdb.media.person;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.media.command.SavePerson;

/// JAX-RS resource exposing the people collection over HTTP. Translates
/// [SavePerson] commands into [PersonService] calls and shapes the response
/// envelope.
@Path("/people")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class PersonResource {
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonService service;

  /// Creates or updates a person from the request body and returns the persisted
  /// representation.
  ///
  /// @param command the validated person payload
  /// @return a `200 OK` response carrying the [PersonResponse]
  @POST
  public Response save(@NotNull @Valid final SavePerson command) {
    var person = service.save(command);   
    return Response.ok()
        .entity(mapper.toPersonResponse(person))
        .build();
  } 
}
