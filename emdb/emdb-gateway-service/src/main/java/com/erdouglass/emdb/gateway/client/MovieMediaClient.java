package com.erdouglass.emdb.gateway.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.erdouglass.emdb.common.query.MovieDto;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MovieMediaClient {
  
  @GET
  @Path("{id}")
  public MovieDto findById(@PathParam("id") Long id);
  
}
