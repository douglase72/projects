package com.erdouglass.emdb.scraper.client;

import java.io.InputStream;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "tmdb-image")
public interface TmdbImageClient {
  
  @GET
  @Path("/{name}")
  public InputStream findByName(@PathParam("name") String name);

}
