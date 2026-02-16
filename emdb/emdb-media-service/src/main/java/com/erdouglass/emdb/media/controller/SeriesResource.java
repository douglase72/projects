package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.service.SeriesService;

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
    var series = service.findById(id, append);
    return mapper.toSeriesDto(series);
  }
  
  @PUT
  @Path("/{id}")
  public SeriesDto update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid UpdateSeries command) {
    var series = service.update(id, command);
    return mapper.toSeriesDto(series);
  }
  
  @DELETE
  @Path("/{id}")
  public Response deleteById(@PathParam("id") @NotNull @Positive Long id) {
    service.deleteById(id);
    return Response.noContent().build();
  }

}
