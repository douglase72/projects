package com.erdouglass.emdb.ingest.ws.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "tmdb-movie")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
public interface TmdbMovieClient {

  @GET
  @Path("/movie/{id}")
  public TmdbMovie findById(
      @PathParam("id") Integer id, 
      @QueryParam("append_to_response") String append);  
}
