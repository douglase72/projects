package com.erdouglass.emdb.media.controller;

import java.util.UUID;

import jakarta.inject.Inject;

import com.erdouglass.emdb.media.mapper.SeriesCreditMapper;
import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.proto.v1.DeleteSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.FindSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesServiceGrpc;
import com.erdouglass.emdb.media.proto.v1.UpdateRoleRequest;
import com.erdouglass.emdb.media.proto.v1.UpdateRoleResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesCreditRequest;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesCreditResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesRequest;
import com.erdouglass.emdb.media.service.SeriesCreditService;
import com.erdouglass.emdb.media.service.SeriesService;
import com.google.protobuf.Empty;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;

@GrpcService
public class SeriesResource extends SeriesServiceGrpc.SeriesServiceImplBase {
  
  @Inject
  SeriesCreditService creditService;
  
  @Inject
  SeriesCreditMapper creditMapper;
  
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
  
  @Override
  @RunOnVirtualThread
  public void findById(FindSeriesRequest request, StreamObserver<SeriesResponse> responseObserver) {
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
  public void delete(DeleteSeriesRequest request, StreamObserver<Empty> responseObserver) {
    service.delete(request.getId()); 
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void updateCredit(
      UpdateSeriesCreditRequest request, 
      StreamObserver<UpdateSeriesCreditResponse> responseObserver) {
    var credit = creditService.update(
        request.getSeriesId(), 
        UUID.fromString(request.getCreditId()),
        creditMapper.toUpdateSeriesCredit(request));
    var response = creditMapper.toUpdateSeriesCreditResponse(credit);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void updateRole(
      UpdateRoleRequest request, 
      StreamObserver<UpdateRoleResponse> responseObserver) {
    var role = creditService.updateRole(
        request.getSeriesId(), 
        UUID.fromString(request.getCreditId()), 
        UUID.fromString(request.getRoleId()),
        creditMapper.toUpdateRole(request));
    var response = creditMapper.toUpdateRoleResponse(role);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
}
