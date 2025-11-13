package com.erdouglass.emdb.scraper.controller;

import com.erdouglass.emdb.scraper.dto.IngestRequest;
import com.erdouglass.emdb.scraper.dto.SynchronizeRequest;
import com.erdouglass.emdb.scraper.producer.TmdbMovieScraper;

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

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {
	
  @Inject
  TmdbMovieScraper scraper;
	
  @POST
  @Path("/ingest")
  public Response ingest(@NotNull @Valid IngestRequest request) {
    scraper.ingest(request.tmdbId());
    return Response.status(Status.ACCEPTED).build();
  }
  
  @POST
  @Path("/synchronize")
  public Response synchronize(@NotNull @Valid SynchronizeRequest request) {
    scraper.synchronize(request.emdbId(), request.tmdbId());
    return Response.status(Status.ACCEPTED).build();
  }

}
