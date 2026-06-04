package com.erdouglass.emdb.media.series;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.media.command.SaveSeries;

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

  @POST
  public Response save(@NotNull @Valid final SaveSeries command) {
    var series = service.save(command);
    return Response.ok()
        .entity(mapper.toSeriesResponse(series))
        .build();
  }
}
