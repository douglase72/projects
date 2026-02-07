package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.service.SeriesService;
import com.erdouglass.webservices.ResourceNotFoundException;

@Path("/series")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeriesResource {
  
  @Inject
  SeriesMapper mapper;

  @Inject
  SeriesService service;
  
  @POST
  public SeriesDto save(@NotNull @Valid SaveSeries command) {
    var series = service.save(command);
    return mapper.toSeriesDto(series);
  }
  
  @GET
  @Path("/{id}")
  public SeriesDto findById(
      @PathParam("id") @NotNull @Positive Long id,
      @QueryParam(Configuration.APPEND) String append) {
    var series = service.findById(id, append)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));
    return mapper.toSeriesDto(series);
  }  

}
