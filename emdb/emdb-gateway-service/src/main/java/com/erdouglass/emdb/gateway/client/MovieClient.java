package com.erdouglass.emdb.gateway.client;

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

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieDto;

import io.quarkus.oidc.token.propagation.common.AccessToken;

@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MovieClient {
  
  @POST
  @AccessToken
  public Response save(SaveMovie command); 
  
  @GET
  @Path("/{id}")
  public MovieDto findById(@PathParam("id") Long id, @QueryParam(Configuration.APPEND) String append);
  
  @PUT
  @Path("/{id}")
  @AccessToken
  public MovieDto update(@PathParam("id") Long id, UpdateMovie command);
  
  @DELETE
  @Path("/{id}")
  @AccessToken
  public Response deleteById(@PathParam("id") Long id);
  
}
