package com.erdouglass.emdb.gateway.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.PersonCreateDto;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.common.request.PersonUpdateRequest;

@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PersonClient {

  @POST
  public Response create(PersonCreateDto request);
  
  @GET
  @Path("{id}")
  public PersonDto findById(@PathParam("id") Long id, @QueryParam(Configuration.APPEND) String append);
  
  @PATCH
  @Path("{id}")
  public PersonDto update(@PathParam("id") Long id, PersonUpdateRequest request);
  
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") Long id);
  
}
