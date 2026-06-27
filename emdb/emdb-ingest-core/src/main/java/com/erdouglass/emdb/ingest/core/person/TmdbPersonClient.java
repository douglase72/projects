package com.erdouglass.emdb.ingest.core.person;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "tmdb-person")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
interface TmdbPersonClient {

  @GET
  @Path("/person/{id}")
  public TmdbPerson findById(@PathParam("id") Integer id);    
}
