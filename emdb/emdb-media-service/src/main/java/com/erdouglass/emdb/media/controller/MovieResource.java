package com.erdouglass.emdb.media.controller;

import jakarta.annotation.security.RolesAllowed;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.dto.SaveResult.Status;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;
import com.erdouglass.webservices.ResourceNotFoundException;

/// REST controller for managing movie resources in the EMDB application.
///
/// Exposes endpoints to create, retrieve, update, and delete movies.
@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieService service;
  
  /// Creates a new movie or updates an existing one based on the provided payload.
  ///
  /// If a new movie is created, it returns a `201 Created` status with a `Location` 
  /// header pointing to the new resource. If an existing movie is updated, it returns 
  /// a `200 OK` status. Both responses include the saved [MovieDto] in the body.
  ///
  /// @param command the validated [SaveMovie] payload containing the movie details
  /// @param uriInfo context object used to build the `Location` URI for new resources
  /// @return a [Response] containing the appropriate HTTP status and [MovieDto] payload
  @POST
  @RolesAllowed(Configuration.ADMIN)
  public Response save(@NotNull @Valid SaveMovie command, @Context UriInfo uriInfo) {
    var result = service.save(command);
    if (result.status() == Status.CREATED) {
      var location = uriInfo.getAbsolutePathBuilder()
          .path(String.valueOf(result.entity().getId()))
          .build();
      return Response.created(location).entity(mapper.toMovieDto(result.entity())).build();
    }
    return Response.ok(mapper.toMovieDto(result.entity())).build();
  } 
  
  /// Retrieves a specific movie by its internal identifier.
  ///
  /// @param id     the strictly positive internal ID of the movie
  /// @param append optional configuration string to append external data 
  /// @return the requested [MovieDto]
  @GET
  @Path("/{id}")
  @RolesAllowed(Configuration.USER)
  public MovieDto findById(
      @PathParam("id") @NotNull @Positive Long id,
      @QueryParam(Configuration.APPEND) String append) {
    var movie = service.findById(id, append)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    return mapper.toMovieDto(movie);
  }
  
}
