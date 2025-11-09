package com.erdouglass.emdb.media.controller;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.service.MovieService;

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
import jakarta.ws.rs.core.UriBuilder;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
	
	@Inject
	MovieService service;
	
	@POST
	public Response create(@NotNull @Valid MovieCreateCommand request) {
		var movie = service.create(request);
    var location = UriBuilder.fromResource(MovieResource.class)
        .path("/{id}")
        .resolveTemplate("id", movie.id())
        .build();
    return Response.created(location).entity(movie).build();
	}
	
  @GET
  @Path("{id}")
  public MovieDto findById(@PathParam("id") @NotNull @Positive Long id) {
  	return service.findById(id);
  }

}
