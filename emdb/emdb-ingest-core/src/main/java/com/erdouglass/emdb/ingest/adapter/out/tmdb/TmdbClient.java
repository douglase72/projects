package com.erdouglass.emdb.ingest.adapter.out.tmdb;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "tmdb-media")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
interface TmdbClient {

  @GET
  @Path("/movie/{id}")
  public TmdbMovieResponse findMovieById(
      @PathParam("id") Integer id, 
      @QueryParam("append_to_response") String append);
}
