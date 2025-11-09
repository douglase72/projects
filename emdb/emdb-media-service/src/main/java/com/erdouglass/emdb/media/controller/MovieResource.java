package com.erdouglass.emdb.media.controller;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.command.MovieUpdateCommand;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;

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

/// The JAX-RS resource (controller) for handling all HTTP requests related to Movies.
/// This class defines the `/movies` API endpoint.
///
/// It orchestrates all standard CRUD (Create, Read, Update, Delete) operations.
/// Following best practices, this resource is responsible for:
///
/// * Accepting and validating incoming DTOs (e.g., `MovieCreateCommand`, `MovieUpdateCommand`).
/// * Delegating all business logic to the `MovieService`.
/// * Mapping persistent `Movie` entities to client-safe `MovieDto` objects for responses.
/// * Returning appropriate HTTP responses and status codes (e.g., `201 Created` with a `Location` header).
///
/// @see com.erdouglass.emdb.media.service.MovieService
/// @see com.erdouglass.emdb.media.mapper.MovieMapper
/// @see com.erdouglass.emdb.common.command.MovieCreateCommand
/// @see com.erdouglass.emdb.common.command.MovieUpdateCommand
/// @see com.erdouglass.emdb.common.query.MovieDto
@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
	
	@Inject
	MovieMapper mapper;
	
	@Inject
	MovieService service;
	
	@POST
	public Response create(@NotNull @Valid MovieCreateCommand request) {
		var movie = service.create(mapper.toMovie(request));
    var location = UriBuilder.fromResource(MovieResource.class)
        .path("/{id}")
        .resolveTemplate("id", movie.id())
        .build();
    return Response.created(location).entity(mapper.toMovieDto(movie)).build();
	}
	
  @GET
  @Path("{id}")
  public MovieDto findById(@PathParam("id") @NotNull @Positive Long id) {
  	return mapper.toMovieDto(service.findById(id));
  }
  
  @PATCH
  @Path("{id}")
  public MovieDto update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid MovieUpdateCommand request) {
    var movie = service.update(id, request);
    return mapper.toMovieDto(movie);
  }
  
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    service.delete(id);
    return Response.noContent().build();
  }

}
