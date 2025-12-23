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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.common.request.MovieCreateRequest;
import com.erdouglass.emdb.common.request.MovieUpdateRequest;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;

/// JAX-RS Resource handling HTTP requests for the Movie domain.
///
/// This resource exposes endpoints at {@code /movies} to perform CRUD operations.
/// It delegates business logic to {@link MovieService} and handles the mapping
/// between Domain Entities and Data Transfer Objects (DTOs).
@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
  public static final String APPEND = "append";
  
  @Inject
  MovieCreditResource creditResource;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieService service;
  
  /// Handles HTTP POST requests to create a new movie.
  ///
  /// On success, returns a {@code 201 Created} status. The response includes
  /// a {@code Location} header pointing to the newly created resource and
  /// the created object in the body.
  ///
  /// @param request the payload containing movie creation data. Must be valid.
  /// @return a {@link Response} containing the created {@link MovieDto} and location header.
  @POST
  public Response create(@NotNull @Valid MovieCreateRequest request) {
    var movie = service.create(mapper.toMovieCreateMessage(request));
    var location = UriBuilder
        .fromResource(MovieResource.class)
        .path("/{id}")
        .resolveTemplate("id", movie.id())
        .build();
    return Response.created(location).entity(mapper.toMovieDto(movie)).build();
  }
  
  @GET
  @Path("{id}")
  public MovieDto findById(
      @PathParam("id") @NotNull @Positive Long id,
      @QueryParam(APPEND) String append) {
    return mapper.toMovieDto(service.findById(id, append));
  }
  
  /// Handles HTTP PATCH requests to partially update a movie.
  ///
  /// Only fields provided in the request body will be updated. Fields omitted
  /// or set to null in the {@link MovieUpdateRequest} remain unchanged.
  ///
  /// @param id      the ID of the movie to update.
  /// @param request the payload containing fields to update.
  /// @return the updated {@link MovieDto}.
  @PATCH
  @Path("{id}")
  public MovieDto update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid MovieUpdateRequest request) {
    var movie = service.update(id, request);
    return mapper.toMovieDto(movie);
  }
  
  /// Handles HTTP DELETE requests to remove a movie.
  ///
  /// On success, returns a {@code 204 No Content} status.
  ///
  /// @param id the ID of the movie to delete.
  /// @return a {@link Response} indicating successful deletion.
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    service.delete(id);
    return Response.noContent().build();
  }
  
  @Path("/{id}/credits")
  public MovieCreditResource credits() {
    return creditResource;
  } 

}
