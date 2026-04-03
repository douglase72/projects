package com.erdouglass.emdb.gateway.controller;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.UpdateRole;
import com.erdouglass.emdb.common.comand.UpdateSeriesCredit;
import com.erdouglass.emdb.common.query.RoleDto;
import com.erdouglass.emdb.common.query.SeriesCreditDto;
import com.erdouglass.emdb.gateway.mapper.SeriesCreditMapper;
import com.erdouglass.emdb.media.proto.v1.SeriesServiceGrpc;

import io.quarkus.grpc.GrpcClient;

@Path("/series/{id}/credits")
@RolesAllowed(Configuration.ADMIN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timeout(value = Configuration.GATEWAY_TIMEOUT, unit = ChronoUnit.SECONDS)
@CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
public class SeriesCreditResource {
  
  @Inject
  SeriesCreditMapper mapper;
  
  @GrpcClient("media-service")
  SeriesServiceGrpc.SeriesServiceBlockingStub service;

  @PUT
  @Path("/{creditId}")
  public SeriesCreditDto update(
      @PathParam("id") @NotNull @Positive Long id,
      @PathParam("creditId") @NotNull UUID creditId,
      @NotNull @Valid UpdateSeriesCredit command) {
    var request = mapper.toUpdateSeriesCreditRequest(id, creditId, command);
    var response = service.updateCredit(request);
    return mapper.toSeriesCreditDto(response);
  }
  
  @PUT
  @Path("/{creditId}/roles/{roleId}")
  public RoleDto update(
      @PathParam("id") @NotNull @Positive Long id,
      @PathParam("creditId") @NotNull UUID creditId,
      @PathParam("roleId") @NotNull UUID roleId,
      @NotNull @Valid UpdateRole command) {
    var request = mapper.toUpdateRoleRequest(id, creditId, roleId, command);
    var response = service.updateRole(request);
    return mapper.toRoleDto(response);
  }  
  
}
