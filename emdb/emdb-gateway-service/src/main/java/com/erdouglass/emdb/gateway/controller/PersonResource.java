package com.erdouglass.emdb.gateway.controller;

import java.time.temporal.ChronoUnit;
import java.util.List;

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

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.gateway.mapper.PersonMapper;
import com.erdouglass.emdb.media.proto.v1.DeletePersonRequest;
import com.erdouglass.emdb.media.proto.v1.PersonServiceGrpc;
import com.erdouglass.webservices.ResponseStatus;

import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;

@Path("/people")
@RolesAllowed(Configuration.ADMIN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timeout(value = Configuration.GATEWAY_TIMEOUT, unit = ChronoUnit.SECONDS)
@CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
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
    var results = service.saveAll(request);
    var response = mapper.toMultiResponseDto(results.getResultsList());
    return Response.status(ResponseStatus.MULTI_STATUS)
        .entity(response)
        .build();
  }
  
  @PermitAll
  @GET
  @Path("/{id}")
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public PersonDto findById(
      @PathParam("id") @NotNull @Positive Long id, 
      @QueryParam(Configuration.APPEND) String append) {
    var request = mapper.toFindPersonRequest(id, append);
    var response = service.findById(request);
    return mapper.toPersonDto(response);
  }
  
  @PUT
  @Path("/{id}")
  public PersonDto update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid UpdatePerson command) {
    var request = mapper.toUpdatePersonRequest(id, command);
    var response = service.update(request);
    return mapper.toPersonDto(response);
  }
  
  @DELETE
  @Path("/{id}")
  @Retry(maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS)
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    var request = DeletePersonRequest.newBuilder().setId(id).build();
    service.delete(request);
    return Response.noContent().build();
  }  

}
