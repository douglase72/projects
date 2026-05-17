package com.erdouglass.emdb.ingest.movie;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/// MicroProfile REST client for the TMDB movie API. The base URL is
/// configured under the `tmdb-movie` config key, and every request carries
/// a bearer token sourced from the `tmdb.token` property.
@RegisterRestClient(configKey = "tmdb-movie")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
interface MovieClient {

  /// Fetches a single movie by its TMDB identifier, optionally appending
  /// related sub-resources in the same response.
  ///
  /// @param id the TMDB movie identifier
  /// @param append a comma-separated list of related resources to include
  ///               (e.g., `"credits"`, `"credits,images"`); may be `null`
  /// @return the deserialized [Movie] payload  
  @GET
  @Path("/movie/{id}")
  Movie findById(
      @PathParam("id") Integer id, 
      @QueryParam("append_to_response") String append);
}
