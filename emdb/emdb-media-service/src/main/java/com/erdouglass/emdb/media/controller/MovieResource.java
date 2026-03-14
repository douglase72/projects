package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;

import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.proto.v1.FindMovieRequest;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.MovieServiceGrpc;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;
import com.erdouglass.emdb.media.proto.v1.SaveMovieResponse;
import com.erdouglass.emdb.media.service.MovieService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;

@GrpcService
public class MovieResource extends MovieServiceGrpc.MovieServiceImplBase {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieService service;
  
  @Override
  @RunOnVirtualThread
  public void save(SaveMovieRequest request, StreamObserver<SaveMovieResponse> responseObserver) {
    var command = mapper.toSaveMovie(request);
    var movie = service.save(command);
    SaveMovieResponse response = mapper.toSaveMovieResponse(movie);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void findById(FindMovieRequest request, StreamObserver<MovieResponse> responseObserver) {
    service.findById(request.getId(), request.getAppend())
        .ifPresentOrElse(m -> {
          var response = mapper.toMovieResponse(m);
          responseObserver.onNext(response);
          responseObserver.onCompleted();
        }, () -> {
          responseObserver.onError(
              Status.NOT_FOUND
                  .withDescription("Movie not found with id: " + request.getId())
                  .asRuntimeException()
          );          
        });
  }

}
