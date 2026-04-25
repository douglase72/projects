package com.erdouglass.emdb.gateway.controller;

import java.time.temporal.ChronoUnit;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieDetails;
import com.erdouglass.emdb.common.query.MovieView;
import com.erdouglass.emdb.gateway.mapper.MovieMapper;
import com.erdouglass.emdb.gateway.query.MovieQueryParams;
import com.erdouglass.emdb.gateway.query.Slice;
import com.erdouglass.emdb.media.proto.v1.DeleteRequest;
import com.erdouglass.emdb.media.proto.v1.MovieServiceGrpc.MovieServiceBlockingStub;

import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.common.annotation.RunOnVirtualThread;

/// REST resource for movie operations.
///
/// Delegates to the media-service via gRPC. Write operations require the
/// admin role; read operations are public.
@Path("/movies")
@RunOnVirtualThread
@RolesAllowed(Configuration.ADMIN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timeout(value = Configuration.GATEWAY_TIMEOUT, unit = ChronoUnit.SECONDS)
@CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
public class MovieResource {
  
  @Inject
  MovieMapper mapper;
  
  @GrpcClient("media-service")
  MovieServiceBlockingStub service;
  
  /// Creates a movie if it does not already exist, otherwise updates the
  /// existing movie matched by TMDB ID.
  ///
  /// @param command the movie data to save
  /// @return 201 Created with [MovieDetails] if new, 200 OK if updated,
  ///         or 204 No Content if unchanged
  @POST
  public Response save(@NotNull @Valid SaveMovie command) {
    var request = mapper.toSaveMovieRequest(command);
    var response = service.save(request);
    return Response.status(mapper.mapProtoStatusToHttpCode(response.getStatus()))
        .entity(mapper.toMovieDetails(response.getMovie()))
        .build();    
  }
  
  /// Returns a paginated list of [MovieView] projections.
  ///
  /// Results are sorted by score descending by default. The response
  /// does not include total counts to avoid an expensive count query
  /// on large tables. Clients should use [Slice#hasNext()] to determine
  /// if more pages are available.
  ///
  /// @param parameters pagination and sorting options
  /// @return a [Slice] of [MovieView] projections
  @GET
  @PermitAll
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public Slice<MovieView> findAll(@BeanParam @Valid MovieQueryParams parameters) {
    var request = mapper.toFindAllMovieRequest(parameters);
    var response = mapper.toPage(service.findAll(request));
    return response;
  }
  
  /// Returns the full details of a single movie.
  ///
  /// The optional `append` query parameter controls which associations
  /// are included in the response (e.g. `credits`).
  ///
  /// @param id the movie's primary key
  /// @param append optional comma-separated list of associations to include
  /// @return the [MovieDetails] for the requested movie
  @GET
  @PermitAll
  @Path("/{id}")
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public MovieDetails findById(
      @PathParam("id") @NotNull @Positive Long id, 
      @QueryParam(Configuration.APPEND) String append) {
    var request = mapper.toFindRequest(id, append);
    var response = mapper.toMovieDetails(service.findById(request));
    return response;
  }
  
  /// Updates an existing movie by primary key with new field values.
  ///
  /// @param id the movie's primary key
  /// @param command the fields to update
  /// @return the updated [MovieDetails]
  @PUT
  @Path("/{id}")
  public MovieDetails update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid UpdateMovie command) {
    var request = mapper.toUpdateMovieRequest(id, command);
    var response = service.update(request);
    return mapper.toMovieDetails(response);
  }
  
  /// Deletes a movie by primary key.
  ///
  /// @param id the movie's primary key
  /// @return 204 No Content on success
  @DELETE
  @Path("/{id}")
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    var request = DeleteRequest.newBuilder().setId(id).build();
    service.delete(request);
    return Response.noContent().build();
  }    
}
