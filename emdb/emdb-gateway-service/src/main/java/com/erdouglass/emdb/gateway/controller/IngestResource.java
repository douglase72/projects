package com.erdouglass.emdb.gateway.controller;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import jakarta.annotation.security.PermitAll;
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
import jakarta.ws.rs.sse.OutboundSseEvent;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.gateway.mapper.IngestMapper;
import com.erdouglass.emdb.gateway.messaging.MediaProducer;
import com.erdouglass.emdb.gateway.query.IngestHistory;
import com.erdouglass.emdb.gateway.query.OffsetPage;
import com.erdouglass.emdb.gateway.service.IngestService;
import com.erdouglass.emdb.notification.proto.v1.IngestServiceGrpc.IngestServiceBlockingStub;

import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Multi;

/// REST resource for asynchronous media ingestion.
///
/// Accepts an [IngestMedia] command, publishes it to the message broker,
/// and immediately returns 202 Accepted with a correlation ID. The actual
/// extraction from TMDB and persistence is handled by the media-service's
/// [MediaConsumer]. Read endpoints proxy to the notification-service over
/// gRPC; the live stream is served from this service via SSE. Most endpoints
/// require the admin role; the SSE stream is open to all authenticated users.
@Path("/ingests")
@RolesAllowed(Configuration.ADMIN)
public class IngestResource {
  
  @Inject
  IngestService ingestService;
  
  @Inject
  IngestMapper mapper;
  
  @Inject
  MediaProducer producer;
  
  @GrpcClient("notification-service")
  IngestServiceBlockingStub service;
  
  /// Submits a media ingestion job for asynchronous processing.
  ///
  /// Generates a correlation ID, publishes the command to the broker, and
  /// returns immediately. Job progress can be observed via [#findById]
  /// or the SSE [#stream].
  ///
  /// @param command the ingestion request containing the TMDB ID, media type, and source
  /// @return 202 Accepted with the generated correlation ID as the response body
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Timeout(value = Configuration.GATEWAY_TIMEOUT, unit = ChronoUnit.SECONDS)
  @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
  public Response create(@NotNull @Valid IngestMedia command) {
    var correlationId = producer.ingest(command);
    return Response.status(Status.ACCEPTED).entity(correlationId).build();
  }
  
  /// Returns a page of ingests sorted by last-modified time, descending.
  ///
  /// @param page one-based page number
  /// @param size page size
  /// @return a page of ingests in their current state
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Timeout(value = Configuration.GATEWAY_TIMEOUT, unit = ChronoUnit.SECONDS)
  @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public OffsetPage<IngestStatusChanged> findAll(
      @QueryParam("page") @Positive Integer page,
      @QueryParam("size") @Positive Integer size) {
    var request = mapper.toFindAllRequest(page, size);
    var response = mapper.toOffsetPage(service.findAll(request));
    return response;
  }
  
  /// Returns the current state of a single ingest.
  ///
  /// @param id the ingest correlation ID
  /// @return the ingest in its most recent known state
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Timeout(value = Configuration.GATEWAY_TIMEOUT, unit = ChronoUnit.SECONDS)
  @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public IngestStatusChanged findById(@PathParam("id") @NotNull UUID id) {
    var request = mapper.toFindByIdRequest(id);
    var response = mapper.toIngestStatusChanged(service.findById(request));
    return response;
  }
  
  /// Returns the full status-change history for a single ingest.
  ///
  /// Each entry represents a transition (Submitted, Started, Extracted,
  /// Completed, Failed) recorded by the notification-service. Ordered by
  /// occurrence time ascending.
  ///
  /// @param id the ingest correlation ID
  /// @return the ingest's history wrapped with its ID
  @GET
  @Path("/{id}/history")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Timeout(value = Configuration.GATEWAY_TIMEOUT, unit = ChronoUnit.SECONDS)
  @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public IngestHistory findHistory(@PathParam("id") @NotNull UUID id) {
    var request = mapper.toFindHistoryRequest(id);
    var response = mapper.toIngestHistory(service.findHistory(request));
    return response;
  }
  
  /// Streams ingest status changes to subscribed clients in real time.
  ///
  /// Each event on the broker fans out to every connected client. Clients
  /// receive only events that occur after they connect; for prior state,
  /// they should also call [#findAll] on page load and merge the results.
  /// Authentication is required but no role is enforced — any signed-in
  /// user may observe the stream.
  /// 
  /// Authentication is required at the HTTP layer (the user must be signed in),
  /// but no role is enforced. The browser's native EventSource cannot attach
  /// bearer tokens, so a stricter @RolesAllowed would block legitimate clients.
  /// Switch to a fetch-based SSE client (e.g. @microsoft/fetch-event-source)
  /// before tightening this.
  ///
  /// @return a Multi of SSE events; one per ingest status change, plus
  ///         periodic keepalive comments
  @GET
  @PermitAll
  @Path("/stream")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public Multi<OutboundSseEvent> stream() {
    return ingestService.stream();
  }
}
