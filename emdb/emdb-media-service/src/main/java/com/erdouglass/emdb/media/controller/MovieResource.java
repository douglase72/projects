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

  /// Handles the HTTP `GET` request to find a movie by its natural
  /// business key (TMDB ID).
  ///
  /// This endpoint allows for retrieval using the external identifier
  /// (e.g., from The Movie Database) instead of the internal surrogate key.
  ///
  /// @param tmdbId The TMDB identifier (`tmdbId`) of the movie, passed as a
  ///               URL path parameter.
  /// @return The corresponding `MovieDto`.
  /// @throws com.erdouglass.emdb.exception.ResourceNotFoundException
  ///         (which is mapped to a 404 response) if the movie does not exist.
  @GET
  @Path("tmdb/{tmdbId}")
  public MovieDto findByTmdbId(@PathParam("tmdbId") @NotNull @Positive Integer tmdbId) {
    return mapper.toMovieDto(service.findByTmdbId(tmdbId));
  }

  /// Handles the HTTP `PATCH` request to partially update an existing movie.
  ///
  /// It validates the incoming `MovieUpdateCommand` and applies its
   /// present fields to the movie found by the given ID.
  ///
  /// @param id The primary key (`id`) of the movie to update.
  /// @param request The `MovieUpdateCommand` DTO containing the fields to update.
  /// @return The fully updated `MovieDto`.
  /// @throws com.erdouglass.emdb.exception.ResourceNotFoundException
  ///         (which is mapped to a 404 response) if the movie does not exist.
  @PATCH
  @Path("{id}")
  public MovieDto update(
      @PathParam("id") @NotNull @Positive Long id, @NotNull @Valid MovieUpdateCommand request) {
    var movie = service.update(id, request);
    return mapper.toMovieDto(movie);
  }

  /// Handles the HTTP `DELETE` request to delete a movie by its primary key.
  ///
  /// @param id The primary key (`id`) of the movie to delete.
  /// @return A JAX-RS `Response` with status `204 No Content`.
  /// @throws com.erdouglass.emdb.exception.ResourceNotFoundException
  ///         (which is mapped to a 404 response) if the movie does not exist.
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    service.delete(id);
    return Response.noContent().build();
  }

}
