package com.erdouglass.emdb.gateway.controller;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.gateway.mapper.PersonMapper;
import com.erdouglass.emdb.media.proto.v1.PersonServiceGrpc;
import com.erdouglass.webservices.ResponseStatus;

import io.quarkus.grpc.GrpcClient;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {
  
  @Inject
  PersonMapper mapper;
  
  @GrpcClient("media-service")
  PersonServiceGrpc.PersonServiceBlockingStub service;
  
  @POST
  public Response save(@NotNull @Valid SavePerson command) {
    var request = mapper.toSavePersonRequest(command);
    var response = service.save(request);    
    return Response.status(mapper.mapProtoStatusToHttpCode(response.getStatus()))
        .entity(mapper.toPersonDto(response.getPerson()))
        .build();
  }
  
  @POST
  @Path("/batch")
  public Response saveAll(List<@Valid SavePerson> commands) {
    var request = mapper.toSavePeopleRequest(commands);
    var response = service.saveAll(request);
    return Response.status(ResponseStatus.MULTI_STATUS)
        .entity(mapper.toMultiResponseDto(response.getResultsList()))
        .build();
  }

}
