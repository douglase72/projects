package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;

import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.proto.v1.DeletePersonRequest;
import com.erdouglass.emdb.media.proto.v1.FindPersonRequest;
import com.erdouglass.emdb.media.proto.v1.PeopleResponse;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.PersonServiceGrpc;
import com.erdouglass.emdb.media.proto.v1.SavePeopleRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonResponse;
import com.erdouglass.emdb.media.proto.v1.UpdatePersonRequest;
import com.erdouglass.emdb.media.service.PersonService;
import com.google.protobuf.Empty;

import io.grpc.Status;
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
  
  @Override
  @RunOnVirtualThread
  public void findById(FindPersonRequest request, StreamObserver<PersonResponse> responseObserver) {
    var person = service.findById(request.getId(), request.getAppend()).orElse(null);
    if (person == null) {
      responseObserver.onError(Status.NOT_FOUND
          .withDescription("Person not found with id: " + request.getId())
          .asRuntimeException());
      return;
    }
    var response = mapper.toPersonResponse(person);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void update(UpdatePersonRequest request, StreamObserver<PersonResponse> responseObserver) {
    var command = mapper.toUpdatePerson(request.getCommand());
    var movie = service.update(request.getId(), command);
    var response = mapper.toPersonResponse(movie);
    responseObserver.onNext(response);
    responseObserver.onCompleted();    
  }
  
  @Override
  @RunOnVirtualThread
  public void delete(DeletePersonRequest request, StreamObserver<Empty> responseObserver) {
    service.delete(request.getId()); 
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }

}
