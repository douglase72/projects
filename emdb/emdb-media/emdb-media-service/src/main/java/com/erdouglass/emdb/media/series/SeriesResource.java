package com.erdouglass.emdb.media.series;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.media.command.SaveSeries;
import com.erdouglass.emdb.media.query.SeriesResponse;

/// JAX-RS resource exposing the series collection over HTTP. Translates
/// [SaveSeries] commands into [SeriesService] calls and shapes the
/// response envelope.
@Path("/series")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class SeriesResource {
  
  @Inject
  SeriesMapper mapper;

  @Inject
  SeriesService service;

  /// Creates or updates a series from the request body and returns the persisted
  /// representation.
  ///
  /// @param command the validated movie payload
  /// @return a `200 OK` response carrying the [SeriesResponse]
  @POST
  public SeriesResponse save(@NotNull @Valid final SaveSeries command) {
    return service.save(command);
  }
}
