package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;

import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.PersonServiceGrpc;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.service.PersonService;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;

@GrpcService
public class PersonResource extends PersonServiceGrpc.PersonServiceImplBase{
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonService service;
  
  @Override
  @RunOnVirtualThread
  public void save(SavePersonRequest request, StreamObserver<PersonResponse> responseObserver) {
    var command = mapper.toSavePerson(request);
    var person = service.save(command);
    var response = mapper.toPersonResponse(person);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

}
