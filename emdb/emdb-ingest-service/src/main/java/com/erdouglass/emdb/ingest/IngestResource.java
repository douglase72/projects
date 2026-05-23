package com.erdouglass.emdb.ingest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com.erdouglass.emdb.ingest.api.IngestMedia;
import com.erdouglass.emdb.scheduler.IngestProducer;

import io.smallrye.common.constraint.NotNull;

/// REST resource that produces the [IngestMedia] command for movies on demand.
///
/// Exposes a single `POST /ingest` endpoint that accepts an [IngestMedia]
/// payload, publishes it via [IngestProducer], and returns the generated
/// correlation id with HTTP 202 Accepted so the caller can correlate the
/// asynchronous ingest result.
@Path("/ingest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class IngestResource {
  
  @Inject
  IngestProducer producer;

  /// Publishes an [IngestMedia] command and returns its correlation id.
  ///
  /// @param command the validated ingest command to publish; must be non-null
  /// @return HTTP 202 Accepted with the generated correlation id as the body
  @POST
  public Response create(@NotNull @Valid final IngestMedia command) {
    var correlationId = producer.publish(command);
    return Response.status(Status.ACCEPTED).entity(correlationId).build();
  }
}
