package com.erdouglass.emdb.gateway.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.gateway.messaging.MediaProducer;

/// REST resource for asynchronous media ingestion.
///
/// Accepts an [IngestMedia] command, publishes it to the message broker,
/// and immediately returns 202 Accepted with a job ID. The actual
/// extraction from TMDB and persistence is handled by the media-service's
/// [MediaConsumer]. Requires the admin role.
@Path("/ingest")
@RolesAllowed(Configuration.ADMIN)
public class IngestResource {
  
  @Inject
  MediaProducer producer;
  
  /// Submits a media ingestion job for asynchronous processing.
  ///
  /// @param command the ingestion request containing the TMDB ID, media type, and source
  /// @return 202 Accepted with the generated job ID as the response body
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response ingest(@NotNull @Valid IngestMedia command) {
    var jobId = producer.ingest(command);
    return Response.status(Status.ACCEPTED).entity(jobId).build();
  }
}
