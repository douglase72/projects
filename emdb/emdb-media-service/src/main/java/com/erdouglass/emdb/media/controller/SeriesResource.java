package com.erdouglass.emdb.media.controller;

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
import jakarta.ws.rs.core.UriBuilder;

import com.erdouglass.emdb.common.command.SeriesCreateCommand;
import com.erdouglass.emdb.common.command.SeriesUpdateCommand;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.service.SeriesService;

@Path("/series")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeriesResource {
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  SeriesService service;
  
  @POST
  public Response create(@NotNull @Valid SeriesCreateCommand request) {
    var series = service.create(mapper.toSeries(request));
    var location = UriBuilder
        .fromResource(SeriesResource.class)
        .path("/{id}")
        .resolveTemplate("id", series.id())
        .build();
    return Response.created(location).entity(mapper.toSeriesDto(series)).build();
  }
  
  @GET
  @Path("{id}")
  public SeriesDto findById(@PathParam("id") @NotNull @Positive Long id) {
    return mapper.toSeriesDto(service.findById(id));
  }
  
  @GET
  @Path("tmdb/{tmdbId}")
  public SeriesDto findByTmdbId(@PathParam("tmdbId") @NotNull @Positive Integer tmdbId) {
    return mapper.toSeriesDto(service.findByTmdbId(tmdbId));
  }
  
  @PATCH
  @Path("{id}")
  public SeriesDto update(
      @PathParam("id") @NotNull @Positive Long id, @NotNull @Valid SeriesUpdateCommand request) {
    var movie = service.update(id, request);
    return mapper.toSeriesDto(movie);
  }
  
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    service.delete(id);
    return Response.noContent().build();
  }

}
