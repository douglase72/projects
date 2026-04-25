package com.erdouglass.emdb.gateway.controller;

import java.util.UUID;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.gateway.mapper.IngestMapper;
import com.erdouglass.emdb.gateway.messaging.MediaProducer;
import com.erdouglass.emdb.gateway.query.IngestHistory;
import com.erdouglass.emdb.gateway.query.OffsetPage;
import com.erdouglass.emdb.notification.proto.v1.IngestServiceGrpc.IngestServiceBlockingStub;

import io.quarkus.grpc.GrpcClient;

/// REST resource for asynchronous media ingestion.
///
/// Accepts an [IngestMedia] command, publishes it to the message broker,
/// and immediately returns 202 Accepted with a correlation ID. The actual
/// extraction from TMDB and persistence is handled by the media-service's
/// [MediaConsumer]. Requires the admin role.
@Path("/ingests")
@RolesAllowed(Configuration.ADMIN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IngestResource {
  
  @Inject
  IngestMapper mapper;
  
  @Inject
  MediaProducer producer;
  
  @GrpcClient("notification-service")
  IngestServiceBlockingStub service;
  
  /// Submits a media ingestion job for asynchronous processing.
  ///
  /// @param command the ingestion request containing the TMDB ID, media type, and source
  /// @return 202 Accepted with the generated correlation ID as the response body
  @POST
  public Response create(@NotNull @Valid IngestMedia command) {
    var correlationId = producer.ingest(command);
    return Response.status(Status.ACCEPTED).entity(correlationId).build();
  }
  
  @GET
  public OffsetPage<IngestStatusChanged> findAll(
      @QueryParam("page") @Positive Integer page,
      @QueryParam("size") @Positive Integer size) {
    var request = mapper.toFindAllRequest(page, size);
    var response = mapper.toOffsetPage(service.findAll(request));
    return response;
  }
  
  @GET
  @Path("/{id}")
  public IngestStatusChanged findById(@PathParam("id") @NotNull UUID id) {
    var request = mapper.toFindByIdRequest(id);
    var response = mapper.toIngestStatusChanged(service.findById(request));
    return response;
  }
  
  @GET
  @Path("/{id}/history")
  public IngestHistory findHistory(@PathParam("id") @NotNull UUID id) {
    var request = mapper.toFindHistoryRequest(id);
    var response = mapper.toIngestHistory(service.findHistory(request));
    return response;
  }
}
