package com.erdouglass.emdb.rest;

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

import com.erdouglass.emdb.media.IngestMedia;
import com.erdouglass.emdb.media.IngestService;

@Path("/ingest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IngestResource {
  
  @Inject
  IngestService service;

  @POST
  public Response create(@NotNull @Valid IngestMedia command) {
    var correlationId = service.publish(command);
    return Response.status(Status.ACCEPTED)
        .entity(correlationId)
        .build();
  }
}
