package com.erdouglass.emdb.ingest.ws.rest;

import java.io.InputStream;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "tmdb-image")
public interface TmdbImageClient {

  @GET
  @Path("/{name}")
  InputStream findByName(@PathParam("name") String name);  
}
