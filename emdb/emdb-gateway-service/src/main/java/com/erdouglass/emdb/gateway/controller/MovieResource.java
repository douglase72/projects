package com.erdouglass.emdb.gateway.controller;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.command.IngestRequest;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.gateway.client.MovieMediaClient;
import com.erdouglass.emdb.gateway.client.MovieScraperClient;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  @RestClient
  MovieMediaClient mediaClient;
  
  @Inject
  @RestClient
  MovieScraperClient scraperClient;
  
  @POST
  @Path("/ingest")
  public Response ingest(@NotNull @Valid IngestRequest request) {
    return scraperClient.ingest(request);
  }
  
  @GET
  @Path("{id}")
  public MovieDto findById(@PathParam("id") @NotNull @Positive Long id) {
    return mediaClient.findById(id);
  }

}
