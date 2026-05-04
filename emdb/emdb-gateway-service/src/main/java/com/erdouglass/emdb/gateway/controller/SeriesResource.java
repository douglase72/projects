package com.erdouglass.emdb.gateway.controller;

import java.time.temporal.ChronoUnit;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.BeanParam;
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

import com.erdouglass.emdb.common.api.Configuration;
import com.erdouglass.emdb.gateway.mapper.SeriesMapper;
import com.erdouglass.emdb.gateway.query.SeriesQueryParams;
import com.erdouglass.emdb.gateway.query.Slice;
import com.erdouglass.emdb.media.api.command.SaveSeries;
import com.erdouglass.emdb.media.api.command.UpdateSeries;
import com.erdouglass.emdb.media.api.query.SeriesDetails;
import com.erdouglass.emdb.media.api.query.SeriesView;
import com.erdouglass.emdb.media.proto.v1.DeleteRequest;
import com.erdouglass.emdb.media.proto.v1.SeriesServiceGrpc.SeriesServiceBlockingStub;

import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.common.annotation.RunOnVirtualThread;

/// REST resource for series operations.
///
/// Delegates to the media-service via gRPC. Write operations require the
/// admin role; read operations are public.
@Path("/series")
@RunOnVirtualThread
@RolesAllowed(Configuration.ADMIN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timeout(value = Configuration.GATEWAY_TIMEOUT, unit = ChronoUnit.SECONDS)
@CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
public class SeriesResource {
  
  @Inject
  SeriesMapper mapper;
  
  @GrpcClient("media-service")
  SeriesServiceBlockingStub service;

  @POST
  public Response save(@NotNull @Valid SaveSeries command) {
    var request = mapper.toSaveSeriesRequest(command);
    var response = service.save(request);
    return Response.status(mapper.mapProtoStatusToHttpCode(response.getStatus()))
        .entity(mapper.toSeriesDetails(response.getSeries()))
        .build();
  }
  
  @GET
  @PermitAll
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public Slice<SeriesView> findAll(@BeanParam @Valid SeriesQueryParams parameters) {
    var request = mapper.toFindAllSeriesRequest(parameters);
    var response = mapper.toPage(service.findAll(request));
    return response;
  }
  
  @GET
  @PermitAll
  @Path("/{id}")
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public SeriesDetails findById(
      @PathParam("id") @NotNull @Positive Long id, 
      @QueryParam(Configuration.APPEND) String append) {
    var request = mapper.toFindRequest(id, append);
    var response = service.findById(request);
    return mapper.toSeriesDetails(response);
  }
  
  @PUT
  @Path("/{id}")
  public SeriesDetails update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid UpdateSeries command) {
    var request = mapper.toUpdateSeriesRequest(id, command);
    var response = service.update(request);
    return mapper.toSeriesDetails(response);
  }
  
  @DELETE
  @Path("/{id}")
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    var request = DeleteRequest.newBuilder().setId(id).build();
    service.delete(request);
    return Response.noContent().build();
  }
}
