package com.erdouglass.emdb.ingest.adapter.in.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.ingest.IngestMediaCommand;
import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;

@Path("/ingest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class IngestResource {

  @Inject
  SubmitIngestUseCase ingestUseCase;
  
  @POST
  public Response ingest(@NotNull @Valid IngestMediaRequest request, @Context UriInfo uriInfo) {
    var command = IngestMediaCommand.of(request.tmdbId(), IngestType.from(request.ingestType()));
    var id = ingestUseCase.submit(command);
    return Response.accepted(id.value())
        .location(uriInfo.getAbsolutePathBuilder().path(id.value().toString()).build())
        .build();    
  }
}
