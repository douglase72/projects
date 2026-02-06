package com.erdouglass.emdb.gateway.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.gateway.client.PersonClient;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {

  @Inject
  @RestClient
  PersonClient client;
  
  @POST
  public PersonDto save(SavePerson command) {
    return client.save(command);
  }
  
  @GET
  @Path("/{id}")
  public PersonDto findById(@PathParam("id") Long id, @QueryParam(Configuration.APPEND) String append) {
    return client.findById(id, append);
  }  
  
}
