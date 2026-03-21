package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;

import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesServiceGrpc;
import com.erdouglass.emdb.media.service.SeriesService;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;

@GrpcService
public class SeriesResource extends SeriesServiceGrpc.SeriesServiceImplBase {
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  SeriesService service;

  @Override
  @RunOnVirtualThread
  public void save(SaveSeriesRequest request, StreamObserver<SaveSeriesResponse> responseObserver) {
    var command = mapper.toSaveSeries(request);
    var result = service.save(command);
    var response = mapper.toSaveSeriesResponse(result);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
