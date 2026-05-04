package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;

import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.proto.v1.DeleteRequest;
import com.erdouglass.emdb.media.proto.v1.FindAllSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.FindRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesPageResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesServiceGrpc.SeriesServiceImplBase;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesRequest;
import com.erdouglass.emdb.media.service.SeriesCrudService;
import com.google.protobuf.Empty;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;

@GrpcService
public class SeriesResource extends SeriesServiceImplBase {
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  SeriesCrudService service;

  @Override
  @RunOnVirtualThread
  public void save(SaveSeriesRequest request, StreamObserver<SaveSeriesResponse> responseObserver) {
    var command = mapper.toSaveSeries(request);
    var result = service.save(command);
    var response = mapper.toSaveSeriesResponse(result);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void findAll(FindAllSeriesRequest request, StreamObserver<SeriesPageResponse> responseObserver) {
    var parameters = mapper.toSeriesQueryParameters(request);
    var page = service.findAll(parameters);
    var response = mapper.toSeriesPageResponse(page);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void findById(FindRequest request, StreamObserver<SeriesResponse> responseObserver) {
    var series = service.findById(request.getId(), request.getAppend()).orElse(null);
    if (series == null) {
      responseObserver.onError(Status.NOT_FOUND
          .withDescription("Series not found with id: " + request.getId())
          .asRuntimeException());
      return;
    }
    var response = mapper.toSeriesResponse(series);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void update(UpdateSeriesRequest request, StreamObserver<SeriesResponse> responseObserver) {
    var command = mapper.toUpdateSeries(request.getCommand());
    var series = service.update(request.getId(), command);
    var response = mapper.toSeriesResponse(series);
    responseObserver.onNext(response);
    responseObserver.onCompleted();    
  }
  
  @Override
  @RunOnVirtualThread
  public void delete(DeleteRequest request, StreamObserver<Empty> responseObserver) {
    service.delete(request.getId()); 
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
