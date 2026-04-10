package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;

import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.proto.v1.DeleteMovieRequest;
import com.erdouglass.emdb.media.proto.v1.FindAllMovieRequest;
import com.erdouglass.emdb.media.proto.v1.FindMovieRequest;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.MovieServiceGrpc;
import com.erdouglass.emdb.media.proto.v1.PageResponse;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;
import com.erdouglass.emdb.media.proto.v1.SaveMovieResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateMovieRequest;
import com.erdouglass.emdb.media.service.MovieService;
import com.google.protobuf.Empty;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;

/// gRPC service implementation for movie operations.
///
/// Translates gRPC requests from the gateway into domain commands,
/// delegates to [MovieService], and maps the results back to protobuf
/// responses. All methods run on virtual threads to avoid blocking
/// platform threads during database I/O.
@GrpcService
public class MovieResource extends MovieServiceGrpc.MovieServiceImplBase {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieService service;
  
  /// Creates or updates a movie, matched by TMDB ID.
  ///
  /// The response indicates whether the entity was [SaveStatus#CREATED],
  /// [SaveStatus#UPDATED], or [SaveStatus#UNCHANGED].
  @Override
  @RunOnVirtualThread
  public void save(SaveMovieRequest request, StreamObserver<SaveMovieResponse> responseObserver) {
    var command = mapper.toSaveMovie(request);
    var result = service.save(command);
    var response = mapper.toSaveMovieResponse(result);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  /// Returns the full details of a single movie.
  ///
  /// Responds with `NOT_FOUND` if no movie exists with the given id.
  @Override
  @RunOnVirtualThread
  public void findById(FindMovieRequest request, StreamObserver<MovieResponse> responseObserver) {
    var movie = service.findById(request.getId(), request.getAppend()).orElse(null);
    if (movie == null) {
      responseObserver.onError(Status.NOT_FOUND
          .withDescription("Movie not found with id: " + request.getId())
          .asRuntimeException());
      return;
    }
    var response = mapper.toMovieResponse(movie);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  /// Returns a paginated list of [MovieView] projections without total
  /// counts, to avoid an expensive `COUNT(*)` on large tables.
  @Override
  @RunOnVirtualThread
  public void findAll(FindAllMovieRequest request, StreamObserver<PageResponse> responseObserver) {
    var parameters = mapper.toMovieQueryParameters(request);
    var page = service.findAll(parameters);
    var response = mapper.toPageResponse(page);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  /// Updates an existing movie's mutable fields.
  ///
  /// Unlike [#save], this operation requires the movie to already exist
  /// and does not match by TMDB ID — the primary key from the request
  /// is authoritative.
  @Override
  @RunOnVirtualThread
  public void update(UpdateMovieRequest request, StreamObserver<MovieResponse> responseObserver) {
    var command = mapper.toUpdateMovie(request.getCommand());
    var movie = service.update(request.getId(), command);
    var response = mapper.toMovieResponse(movie);
    responseObserver.onNext(response);
    responseObserver.onCompleted();    
  }
  
  /// Deletes a movie by primary key. Returns an empty response on success.
  @Override
  @RunOnVirtualThread
  public void delete(DeleteMovieRequest request, StreamObserver<Empty> responseObserver) {
    service.delete(request.getId()); 
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
