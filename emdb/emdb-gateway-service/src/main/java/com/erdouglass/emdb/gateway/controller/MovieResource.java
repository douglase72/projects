package com.erdouglass.emdb.gateway.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.gateway.client.MovieClient;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  @RestClient
  MovieClient client;
  
  @POST
  public MovieDto save(SaveMovie command) {
    return client.save(command);
  }
  
  @GET
  @Path("/{id}")
  public MovieDto findById(@PathParam("id") Long id, @QueryParam(Configuration.APPEND) String append) {
    return client.findById(id, append);
  } 
  
  @PUT
  @Path("/{id}")
  public MovieDto update(@PathParam("id") Long id, UpdateMovie command) {
    return client.update(id, command);
  }
  
  @DELETE
  @Path("/{id}")
  public Response deleteById(@PathParam("id") Long id) {
    return client.deleteById(id);
  }

}
