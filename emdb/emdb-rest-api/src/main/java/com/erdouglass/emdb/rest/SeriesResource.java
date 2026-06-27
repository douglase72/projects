package com.erdouglass.emdb.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SeriesCommandService;
import com.erdouglass.emdb.media.series.SeriesDto;

@Path("/series")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SeriesResource {
  
  @Inject
  SeriesCommandService service;
  
  @POST
  public SeriesDto save(@NotNull @Valid SaveSeries command) {
    return service.save(command);
  }  
}
