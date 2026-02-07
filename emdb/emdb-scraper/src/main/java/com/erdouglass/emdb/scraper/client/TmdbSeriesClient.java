package com.erdouglass.emdb.scraper.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.erdouglass.emdb.scraper.query.TmdbSeriesDto;

@RegisterRestClient(configKey = "tmdb-series")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
@Produces(MediaType.APPLICATION_JSON)
public interface TmdbSeriesClient {

  @GET
  @Path("/tv/{id}")
  public TmdbSeriesDto findById(@PathParam("id") Integer id, @QueryParam("append_to_response") String append);
  
}
