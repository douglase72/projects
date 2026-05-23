package com.erdouglass.emdb.ingest.scraper.person;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/// REST client for the TMDB person endpoints.
///
/// Authentication is supplied automatically via a bearer token resolved from
/// the `tmdb.token` configuration property.
@RegisterRestClient(configKey = "tmdb-person")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
interface PersonClient {

  /// Fetches a person by their TMDB id.
  ///
  /// @param id the TMDB person id
  /// @return the deserialized [Person] returned by TMDB  
  @GET
  @Path("/person/{id}")
  Person findById(@PathParam("id") Integer id);
}