package com.erdouglass.emdb.scraper.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.erdouglass.emdb.scraper.query.TmdbPersonDto;

@RegisterRestClient(configKey = "tmdb-person") 
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
@Produces(MediaType.APPLICATION_JSON)
public interface TmdbPersonClient {

  @GET
  @Path("/person/{id}")
  public TmdbPersonDto findById(@PathParam("id") Integer id);
  
}
