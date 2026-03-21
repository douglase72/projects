package com.erdouglass.emdb.gateway.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.gateway.mapper.SeriesMapper;
import com.erdouglass.emdb.media.proto.v1.SeriesServiceGrpc;

import io.quarkus.grpc.GrpcClient;

@Path("/series")
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
  
}
