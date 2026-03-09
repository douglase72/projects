package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;

import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.MovieServiceGrpc;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;
import com.erdouglass.emdb.media.service.MovieService;

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
  public void save(SaveMovieRequest request, StreamObserver<MovieResponse> responseObserver) {
    var command = mapper.toSaveMovie(request);
    var movie = service.save(command);
    var response = mapper.toMovieResponse(movie);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

}
