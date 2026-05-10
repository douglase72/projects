package com.erdouglass.emdb.gateway.controller;

import java.time.temporal.ChronoUnit;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.erdouglass.emdb.common.api.Configuration;
import com.erdouglass.emdb.gateway.mapper.UserMapper;
import com.erdouglass.emdb.user.api.command.UpdateUser;
import com.erdouglass.emdb.user.api.query.UserDetails;
import com.erdouglass.emdb.user.proto.v1.FindOrCreateRequest;
import com.erdouglass.emdb.user.proto.v1.UserServiceGrpc.UserServiceBlockingStub;

import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.RunOnVirtualThread;

@Path("/users")
@Authenticated
@RunOnVirtualThread
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timeout(value = Configuration.GATEWAY_TIMEOUT, unit = ChronoUnit.SECONDS)
@CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
public class UserResource {
  
  @Inject
  JsonWebToken jwt;
  
  @Inject
  UserMapper mapper;
  
  @GrpcClient("user-service")
  UserServiceBlockingStub service;
  
  @GET
  @Path("/me")
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public UserDetails findOrCreate() {
    var request = FindOrCreateRequest.newBuilder()
        .setId(jwt.getSubject())
        .setUsername(jwt.getClaim("preferred_username"))
        .setEmail(jwt.getClaim("email"))
        .setFirstName(jwt.getClaim("given_name"))
        .setLastName(jwt.getClaim("family_name"))        
        .build();
    return mapper.toUserDetails(service.findOrCreate(request));
  }
  
  @PUT
  @Path("/me")
  public UserDetails update(@NotNull @Valid UpdateUser command) {
    var request = mapper.toUpdateUserRequest(command);
    var response = service.update(request);
    return mapper.toUserDetails(response);
  } 
}
