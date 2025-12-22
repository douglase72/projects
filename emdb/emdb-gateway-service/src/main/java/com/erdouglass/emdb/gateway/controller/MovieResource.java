package com.erdouglass.emdb.gateway.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.common.request.IngestRequest;
import com.erdouglass.emdb.common.request.MovieCreateRequest;
import com.erdouglass.emdb.common.request.MovieUpdateRequest;
import com.erdouglass.emdb.gateway.client.MovieClient;
import com.erdouglass.emdb.gateway.service.MovieService;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  @RestClient
  MovieClient client;
  
  @Inject
  MovieService service;
  
  @POST
  public Response create(@NotNull @Valid MovieCreateRequest request) {
    return client.create(request);
  }
  
  @POST
  @Path("/cron")
  public Response cron() {
    service.cron();
    return Response.status(Status.ACCEPTED).build();
  }
  
  @POST
  @Path("/ingest")
  public Response ingest(@NotNull @Valid IngestRequest request) {
    var jobId = service.ingest(request.tmdbId());
    return Response.status(Status.ACCEPTED).entity(jobId).build();
  }
  
  @GET
  @Path("{id}")
  public MovieDto findById(@PathParam("id") @NotNull @Positive Long id) {
    return client.findById(id);
  }
  
  @PATCH
  @Path("{id}")
  public MovieDto update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid MovieUpdateRequest request) {
    return client.update(id, request);
  }
  
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    return client.delete(id);
  }

}
