package com.erdouglass.emdb.ingest.scraper.movie;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/// REST client for the TMDB movie endpoints.
///
/// Authentication is supplied automatically via a bearer token resolved from
/// the `tmdb.token` configuration property.
@RegisterRestClient(configKey = "tmdb-movie")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
interface MovieClient {

  /// Fetches a movie by its TMDB id, optionally appending related sub-resources.
  ///
  /// @param id     the TMDB movie id
  /// @param append a comma-separated list of sub-resources to append (for
  ///               example `credits`); may be null
  /// @return the deserialized [Movie] returned by TMDB  
  @GET
  @Path("/movie/{id}")
  Movie findById(
      @PathParam("id") Integer id, 
      @QueryParam("append_to_response") String append);  
}
