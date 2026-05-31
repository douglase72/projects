package com.erdouglass.emdb.media.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.media.domain.SeriesService;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SeriesResponse;

/// JAX-RS resource exposing the series collection over HTTP. Translates
/// [SaveSeries] commands into [SeriesService] calls and shapes the response
/// envelope.
@Path("/series")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SeriesResource {

  @Inject
  SeriesService service;
  
  @Context
  UriInfo uriInfo;
  
  /// Creates or updates a series from the given [SaveSeries] command.
  ///
  /// The underlying service performs an upsert keyed by TMDB identifier, so
  /// this endpoint is idempotent with respect to that identifier.
  ///
  /// @param command the validated save-series payload
  /// @return `201 Created` with a `Location` header pointing to the canonical
  ///         resource URI and a [SeriesResponse] body  
  @POST
  public Response save(@NotNull @Valid final SaveSeries command) {
    var series = service.save(command);
    var location = uriInfo.getAbsolutePathBuilder()
        .path(String.valueOf(series.id()))
        .build();       
    return Response.created(location).entity(series).build();
  }
}
