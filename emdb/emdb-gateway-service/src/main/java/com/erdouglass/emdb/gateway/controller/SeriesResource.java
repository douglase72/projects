package com.erdouglass.emdb.gateway.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
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
import com.erdouglass.emdb.gateway.mapper.SeriesMapper;
import com.erdouglass.emdb.media.proto.v1.DeleteSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SeriesServiceGrpc;

import io.quarkus.grpc.GrpcClient;

@Path("/series")
@RolesAllowed(Configuration.ADMIN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeriesResource {
  
  @Inject
  SeriesMapper mapper;
  
  @GrpcClient("media-service")
  SeriesServiceGrpc.SeriesServiceBlockingStub service;

  @POST
  public Response save(@NotNull @Valid SaveSeries command) {
    var request = mapper.toSaveSeriesRequest(command);
    var response = service.save(request);
    return Response.status(mapper.mapProtoStatusToHttpCode(response.getStatus()))
        .entity(mapper.toSeriesDto(response.getSeries()))
        .build();
  }
  
  @PermitAll
  @GET
  @Path("/{id}")
  public SeriesDto findById(
      @PathParam("id") @NotNull @Positive Long id, 
      @QueryParam(Configuration.APPEND) String append) {
    var response = service.findById(mapper.toFindSeriesRequest(id, append));
    return mapper.toSeriesDto(response);
  }
  
  @PUT
  @Path("/{id}")
  public SeriesDto update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid UpdateSeries command) {
    var request = mapper.toUpdateSeriesRequest(id, command);
    var response = service.update(request);
    return mapper.toSeriesDto(response);
  }
  
  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    var request = DeleteSeriesRequest.newBuilder().setId(id).build();
    service.delete(request);
    return Response.noContent().build();
  }
  
}
