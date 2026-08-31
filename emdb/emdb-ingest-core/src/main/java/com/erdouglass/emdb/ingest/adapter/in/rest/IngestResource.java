package com.erdouglass.emdb.ingest.adapter.in.rest;

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
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.ingest.api.IngestType;
import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

@Path("/ingest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class IngestResource {

  @Inject
  SubmitIngestUseCase submitUseCase;
  
  @Inject
  UriInfo uriInfo;
  
  @POST
  public Response submit(@NotNull @Valid IngestMediaRequest request) {
    var command = IngestMediaCommand
        .of(TmdbId.of(request.tmdbId()), IngestType.from(request.ingestType()));
    var id = submitUseCase.submit(command);
    URI location = uriInfo.getAbsolutePathBuilder()
        .path(id.value().toString())
        .build();
    return Response.accepted(id.value())
        .location(location)
        .build();
  }
}
