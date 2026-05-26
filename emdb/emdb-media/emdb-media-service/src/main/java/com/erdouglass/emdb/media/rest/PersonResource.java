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

import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.media.api.query.PersonResponse;
import com.erdouglass.emdb.media.domain.PersonService;
import com.erdouglass.emdb.media.domain.person.PersonServiceImpl;

/// JAX-RS resource exposing the people collection over HTTP. Translates
/// [SavePerson] commands into [PersonServiceImpl] calls and shapes the response
/// envelope.
@Path("/people")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

  @Inject
  PersonService service;
  
  @Context
  UriInfo uriInfo;

  /// Creates or updates a person from the given [SavePerson] command.
  ///
  /// The underlying service performs an upsert keyed by TMDB identifier, so
  /// this endpoint is idempotent with respect to that identifier.
  ///
  /// @param command the validated save-person payload
  /// @return `201 Created` with a `Location` header pointing to the canonical
  ///         resource URI and a [PersonResponse] body  
  @POST
  public Response save(@NotNull @Valid final SavePerson command) {
    var person = service.save(command);
    var location = uriInfo.getAbsolutePathBuilder()
        .path(String.valueOf(person.id()))
        .build();       
    return Response.created(location).entity(person).build();
  } 
}
