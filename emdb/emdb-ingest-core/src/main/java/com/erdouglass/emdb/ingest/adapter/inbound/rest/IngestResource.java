package com.erdouglass.emdb.ingest.adapter.inbound.rest;

import java.net.URI;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.ingest.application.port.inbound.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.inbound.SubmitIngestUseCase;
import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.SourceId.Source;

@Path("/ingest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IngestResource {

  @Inject
  SubmitIngestUseCase ingestUseCase;
  
  @POST
  public Response create(@NotNull @Valid IngestMediaRequest request) {
    var command = IngestMediaCommand
        .of(SourceId.of(Source.from(request.source()), request.sourceId()), request.type());
    var id = ingestUseCase.submit(command);
    return Response.accepted(id.value())
        .location(URI.create("/api/ingest/" + id.value()))
        .build();
  }  
}
