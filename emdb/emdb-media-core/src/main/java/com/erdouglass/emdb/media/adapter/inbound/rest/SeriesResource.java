package com.erdouglass.emdb.media.adapter.inbound.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveSeries;
import com.erdouglass.emdb.media.SaveSeriesService;
import com.erdouglass.emdb.media.application.port.inbound.series.DeleteSeriesUseCase;
import com.erdouglass.emdb.media.application.port.inbound.series.UpdateSeries;
import com.erdouglass.emdb.media.application.port.inbound.series.UpdateSeriesUseCase;

@Path("/series")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class SeriesResource {
  
  @Inject
  DeleteSeriesUseCase deleteUseCase;

  @Inject
  SaveSeriesService saveService;
  
  @Inject
  UpdateSeriesUseCase updateUseCase;
  
  @POST
  public SaveResult saveSeries(SaveSeries command) {
    return saveService.save(command);
  }
  
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") Long id, UpdateSeries command) {
    updateUseCase.update(id, command);
    return Response.noContent().build();
  }
  
  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") Long id) {
    deleteUseCase.deleteById(id);
    return Response.noContent().build();
  }
}
