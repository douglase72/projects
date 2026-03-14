package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;

import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.proto.v1.PeopleResponse;
import com.erdouglass.emdb.media.proto.v1.PersonServiceGrpc;
import com.erdouglass.emdb.media.proto.v1.SavePeopleRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonResponse;
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
  public void save(SavePersonRequest request, StreamObserver<SavePersonResponse> responseObserver) {
    var command = mapper.toSavePerson(request);
    var result = service.save(command);
    var response = mapper.toSavePersonResponse(result);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void saveAll(SavePeopleRequest request, StreamObserver<PeopleResponse> responseObserver) {
    var commands = mapper.toSavePeople(request.getPeopleList());
    var results = service.saveAll(commands);
    var response = mapper.toPeopleResponse(results);
    responseObserver.onNext(response); 
    responseObserver.onCompleted();
  }

}
