package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.common.request.PersonCreateRequest;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.service.PersonService;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonService service;
  
  @POST
  public Response create(@NotNull @Valid PersonCreateRequest request) {
    var person = service.create(mapper.toPerson(request));
    var location = UriBuilder.fromResource(MovieResource.class)
        .path("/{id}")
        .resolveTemplate("id", person.id())
        .build();
    return Response.created(location).entity(mapper.toPersonDto(person)).build();
  }
  
  @GET
  @Path("{id}")
  public PersonDto findById(@PathParam("id") @NotNull @Positive Long id) {
    return mapper.toPersonDto(service.findById(id));
  }

}
