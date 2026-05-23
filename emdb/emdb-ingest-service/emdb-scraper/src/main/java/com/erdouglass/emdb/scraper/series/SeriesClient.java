package com.erdouglass.emdb.scraper.series;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/// REST client for the TMDB TV (series) endpoints.
///
/// Authentication is supplied automatically via a bearer token resolved from
/// the `tmdb.token` configuration property.
@RegisterRestClient(configKey = "tmdb-series")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
interface SeriesClient {

  /// Fetches a series by its TMDB id, optionally appending related sub-resources.
  ///
  /// @param id     the TMDB series id
  /// @param append a comma-separated list of sub-resources to append (for
  ///               example `aggregate_credits`); may be null
  /// @return the deserialized [Series] returned by TMDB  
  @GET
  @Path("/tv/{id}")
  Series findById(
      @PathParam("id") Integer id, 
      @QueryParam("append_to_response") String append);
}
