package com.erdouglass.emdb.gateway.controller;

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

import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.gateway.service.IngestService;

@Path("/ingest")
public class IngestResource {
  
  @Inject
  IngestService ingestService;
  
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response ingest(@NotNull @Valid IngestMedia command) {
    var jobId = ingestService.ingest(command);
    return Response.status(Status.ACCEPTED).entity(jobId).build();
  }
  
}
