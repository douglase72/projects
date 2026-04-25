package com.erdouglass.emdb.notification.controller;

import java.util.UUID;

import jakarta.inject.Inject;

import com.erdouglass.emdb.notification.mapper.IngestMapper;
import com.erdouglass.emdb.notification.proto.v1.FindAllRequest;
import com.erdouglass.emdb.notification.proto.v1.FindByIdRequest;
import com.erdouglass.emdb.notification.proto.v1.FindHistoryRequest;
import com.erdouglass.emdb.notification.proto.v1.FindHistoryResponse;
import com.erdouglass.emdb.notification.proto.v1.IngestResponse;
import com.erdouglass.emdb.notification.proto.v1.IngestServiceGrpc.IngestServiceImplBase;
import com.erdouglass.emdb.notification.proto.v1.PageResponse;
import com.erdouglass.emdb.notification.service.IngestService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;

@GrpcService
public class IngestResource extends IngestServiceImplBase {
  
  @Inject
  IngestMapper mapper;
  
  @Inject
  IngestService service;

  @Override
  @RunOnVirtualThread
  public void findAll(FindAllRequest request, StreamObserver<PageResponse> responseObserver) {
    var page = service.findAll(request.getPage(), request.getSize());
    var response = mapper.toPageResponse(page);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void findById(FindByIdRequest request, StreamObserver<IngestResponse> responseObserver) {
    var ingest = service.findById(UUID.fromString(request.getId())).orElse(null);
    if (ingest == null) {
      responseObserver.onError(Status.NOT_FOUND
          .withDescription("Ingest not found with id: " + request.getId())
          .asRuntimeException());
      return;
    }
    var response = mapper.toIngestResponse(ingest);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void findHistory(
      FindHistoryRequest request, 
      StreamObserver<FindHistoryResponse> responseObserver) {
    var changes = service.findStatusChanges(UUID.fromString(request.getId()));
    var response = mapper.toFindHistoryResponse(request.getId(), changes);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
