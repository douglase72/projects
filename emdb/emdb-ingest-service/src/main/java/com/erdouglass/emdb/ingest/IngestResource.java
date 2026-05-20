package com.erdouglass.emdb.ingest;

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

/// REST endpoint for on-demand ingest requests.
///
/// Submitted commands are handed to [IngestProducer], which publishes them
/// to RabbitMQ for asynchronous processing. The HTTP call therefore returns
/// as soon as the message has been enqueued — the actual ingestion happens
/// downstream on a consumer.
@Path("/ingest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class IngestResource {
  
  @Inject
  IngestProducer producer;
  
  /// Accepts an ingest command and enqueues it for asynchronous processing.
  ///
  /// The response carries the generated correlation ID in the body so the
  /// caller can stitch together logs, metrics, and downstream events tied
  /// to this submission.
  ///
  /// @param command the ingest request; validated against its bean
  ///                constraints before being accepted
  /// @return `202 Accepted` with the correlation ID assigned by
  ///         [IngestProducer#send]  
  @POST
  public Response create(@NotNull @Valid IngestMedia command) {
    var correlationId = producer.send(command);
    return Response.status(Status.ACCEPTED).entity(correlationId).build();
  }
}
