package com.erdouglass.emdb.ingest.core.series;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "tmdb-series")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
interface TmdbSeriesClient {
  
  @GET
  @Path("/tv/{id}")
  public TmdbSeries findById(
      @PathParam("id") Integer id, 
      @QueryParam("append_to_response") String append);
}
