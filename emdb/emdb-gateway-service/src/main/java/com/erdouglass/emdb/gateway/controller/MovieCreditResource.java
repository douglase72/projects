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
import com.erdouglass.emdb.common.comand.UpdateMovieCredit;
import com.erdouglass.emdb.common.query.MovieCreditDto;
import com.erdouglass.emdb.gateway.mapper.MovieCreditMapper;
import com.erdouglass.emdb.media.proto.v1.MovieServiceGrpc;

import io.quarkus.grpc.GrpcClient;

@Path("/movies/{id}/credits")
@RolesAllowed(Configuration.ADMIN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timeout(value = Configuration.GATEWAY_TIMEOUT, unit = ChronoUnit.SECONDS)
@CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
public class MovieCreditResource {
  
  @Inject
  MovieCreditMapper mapper;
  
  @GrpcClient("media-service")
  MovieServiceGrpc.MovieServiceBlockingStub service;
  
  @PUT
  @Path("/{creditId}")
  public MovieCreditDto update(
      @PathParam("id") @NotNull @Positive Long id,
      @PathParam("creditId") @NotNull UUID creditId,
      @NotNull @Valid UpdateMovieCredit command) {
    var request = mapper.toUpdateMovieCreditRequest(id, creditId, command);
    var response = service.updateCredit(request);
    return mapper.toMovieCreditDto(response);
  }

}
