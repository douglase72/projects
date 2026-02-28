package com.erdouglass.emdb.media.controller;

import java.util.UUID;

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
import com.erdouglass.emdb.common.comand.UpdateSeriesCredit;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.media.service.SeriesService;

@Path("/series")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeriesResource {

  @Inject
  SeriesService service;
  
  @POST
  public SeriesDto save(@NotNull @Valid SaveSeries command) {
    return service.save(command);
  }
  
  @GET
  @Path("/{id}")
  public SeriesDto findById(
      @PathParam("id") @NotNull @Positive Long id,
      @QueryParam(Configuration.APPEND) String append) {
    return service.findById(id, append);
  }
  
  @PUT
  @Path("/{id}")
  public SeriesDto update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid UpdateSeries command) {
    return service.update(id, command);
  }
  
  @DELETE
  @Path("/{id}")
  public Response deleteById(@PathParam("id") @NotNull @Positive Long id) {
    service.deleteById(id);
    return Response.noContent().build();
  }
  
  @PUT
  @Path("/credits/{creditId}")
  public Response updateCredit(
      @PathParam("creditId") @NotNull UUID creditId, 
      @NotNull @Valid UpdateSeriesCredit command) {
    service.updateCredit(creditId, command);
    return Response.noContent().build();
  }

}
