package com.erdouglass.emdb.gateway.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.common.request.PersonCreateRequest;
import com.erdouglass.emdb.common.request.PersonUpdateRequest;
import com.erdouglass.emdb.gateway.client.PersonClient;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {
  
  @Inject
  @RestClient
  PersonClient client;

  @POST
  public Response create(@NotNull @Valid PersonCreateRequest request) {
    return client.create(request);
  }
  
  @GET
  @Path("{id}")
  public PersonDto findById(@PathParam("id") @NotNull @Positive Long id) {
    return client.findById(id);
  }
  
  @PATCH
  @Path("{id}")
  public PersonDto update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid PersonUpdateRequest request) {
    return client.update(id, request);
  }
  
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    return client.delete(id);
  }
  
}
