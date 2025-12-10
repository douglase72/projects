package com.erdouglass.emdb.media.controller;

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

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;

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
@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

  @Inject
  MovieMapper mapper;

  @Inject
  MovieService service;

  /// Handles the HTTP `POST` request to create a new movie.
  ///
  /// It validates the incoming `MovieCreateCommand`, maps it to an entity,
  /// and persists it via the `MovieService`.
  ///
  /// @param request The `MovieCreateCommand` DTO, validated to ensure correctness.
  /// @return A JAX-RS `Response` with status `201 Created`, a `Location`
  ///         header pointing to the new resource, and the created `MovieDto`
  ///         in the response body.
  @POST
  public Response create(@NotNull @Valid MovieCreateCommand request) {
    var movie = service.create(mapper.toMovie(request));
    var location = UriBuilder
        .fromResource(MovieResource.class)
        .path("/{id}")
        .resolveTemplate("id", movie.id())
        .build();
    return Response.created(location).entity(mapper.toMovieDto(movie)).build();
  }

  /// Handles the HTTP `GET` request to find a movie by its primary (surrogate) key.
  ///
  /// @param id The primary key (`id`) of the movie, passed as a URL path parameter.
  /// @return The corresponding `MovieDto`.
  /// @throws com.erdouglass.emdb.exception.ResourceNotFoundException
  ///         (which is mapped to a 404 response) if the movie does not exist.
  @GET
  @Path("{id}")
  public MovieDto findById(@PathParam("id") @NotNull @Positive Long id) {
    return mapper.toMovieDto(service.findById(id));
  }

}
