package com.erdouglass.emdb.media.controller;

import java.util.UUID;

import jakarta.inject.Inject;

import com.erdouglass.emdb.media.mapper.MovieCreditMapper;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.proto.v1.DeleteMovieRequest;
import com.erdouglass.emdb.media.proto.v1.FindMovieRequest;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.MovieServiceGrpc;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;
import com.erdouglass.emdb.media.proto.v1.SaveMovieResponse;
import com.erdouglass.emdb.media.proto.v1.UniMovieCreditResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateMovieCreditRequest;
import com.erdouglass.emdb.media.proto.v1.UpdateMovieRequest;
import com.erdouglass.emdb.media.service.MovieCreditService;
import com.erdouglass.emdb.media.service.MovieService;
import com.google.protobuf.Empty;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;

@GrpcService
public class MovieResource extends MovieServiceGrpc.MovieServiceImplBase {
  
  @Inject
  MovieCreditService creditService;
  
  @Inject
  MovieCreditMapper creditMapper;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieService service;
  
  @Override
  @RunOnVirtualThread
  public void save(SaveMovieRequest request, StreamObserver<SaveMovieResponse> responseObserver) {
    var command = mapper.toSaveMovie(request);
    var result = service.save(command);
    var response = mapper.toSaveMovieResponse(result);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
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
  
  @Override
  @RunOnVirtualThread
  public void update(UpdateMovieRequest request, StreamObserver<MovieResponse> responseObserver) {
    var command = mapper.toUpdateMovie(request.getCommand());
    var movie = service.update(request.getId(), command);
    var response = mapper.toMovieResponse(movie);
    responseObserver.onNext(response);
    responseObserver.onCompleted();    
  }
  
  @Override
  @RunOnVirtualThread
  public void delete(DeleteMovieRequest request, StreamObserver<Empty> responseObserver) {
    service.delete(request.getId()); 
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void updateCredit(
      UpdateMovieCreditRequest request, 
      StreamObserver<UniMovieCreditResponse> responseObserver) {
    var credit = creditService.update(
        request.getMovieId(), 
        UUID.fromString(request.getCreditId()), 
        creditMapper.toUpdateMovieCredit(request));
    var response = creditMapper.toUniMovieCreditResponse(credit);
    responseObserver.onNext(response);
    responseObserver.onCompleted();    
  }

}
