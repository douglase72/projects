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

import com.erdouglass.emdb.common.request.IngestRequest;
import com.erdouglass.emdb.gateway.service.MovieService;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  MovieService service;
  
  @POST
  @Path("/ingest")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response ingest(@NotNull @Valid IngestRequest request) {
    var traceId = service.ingest(request.tmdbId());
    return Response.status(Status.ACCEPTED).entity(traceId).build();
  }

}
