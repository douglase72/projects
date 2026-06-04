package com.erdouglass.emdb.media.person.internal;

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

import com.erdouglass.emdb.media.command.SavePerson;
import com.erdouglass.emdb.media.person.PersonResponse;

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
  
  @Context
  UriInfo uriInfo;

  /// Creates or updates a person from the given [SavePerson] command.
  ///
  /// The underlying service performs an upsert keyed by TMDB identifier, so
  /// this endpoint is idempotent with respect to that identifier.
  ///
  /// @param command the validated [SavePerson] command
  /// @return `201 Created` with a `Location` header pointing to the canonical
  ///         resource URI and a [PersonResponse] body  
  @POST
  public Response save(@NotNull @Valid final SavePerson command) {
    var person = service.save(command);
    var location = uriInfo.getAbsolutePathBuilder()
        .path(String.valueOf(person.getId()))
        .build();       
    return Response.created(location)
        .entity(mapper.toPersonResponse(person))
        .build();
  } 
}
