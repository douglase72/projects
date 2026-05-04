package com.erdouglass.emdb.media.controller;

import jakarta.inject.Inject;

import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.proto.v1.DeleteRequest;
import com.erdouglass.emdb.media.proto.v1.FindAllPersonRequest;
import com.erdouglass.emdb.media.proto.v1.FindRequest;
import com.erdouglass.emdb.media.proto.v1.PersonPageResponse;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.PersonServiceGrpc.PersonServiceImplBase;
import com.erdouglass.emdb.media.proto.v1.SavePeopleRequest;
import com.erdouglass.emdb.media.proto.v1.SavePeopleResponse;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonResponse;
import com.erdouglass.emdb.media.proto.v1.UpdatePersonRequest;
import com.erdouglass.emdb.media.service.PersonCrudService;
import com.google.protobuf.Empty;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;

/// gRPC controller for person operations.
///
/// Translates protobuf requests into domain commands, delegates to
/// [PersonCrudService], and maps the results back to protobuf responses.
/// All methods run on virtual threads to allow blocking repository
/// calls without tying up event-loop threads.
@GrpcService
public class PersonResource extends PersonServiceImplBase {
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonCrudService service;
  
  /// Creates or updates a single person matched by TMDB ID.
  @Override
  @RunOnVirtualThread
  public void save(SavePersonRequest request, StreamObserver<SavePersonResponse> responseObserver) {
    var command = mapper.toSavePerson(request);
    var result = service.save(command);
    var response = mapper.toSavePersonResponse(result);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  /// Creates or updates a batch of people in a single call.
  @Override
  @RunOnVirtualThread
  public void saveAll(SavePeopleRequest request, StreamObserver<SavePeopleResponse> responseObserver) {
    var commands = mapper.toSavePeople(request.getPeopleList());
    var results = service.saveAll(commands);
    var response = mapper.toSavePeopleResponse(results);
    responseObserver.onNext(response); 
    responseObserver.onCompleted();
  }  
  
  /// Returns a paginated list of people.
  @Override
  @RunOnVirtualThread
  public void findAll(FindAllPersonRequest request, StreamObserver<PersonPageResponse> responseObserver) {
    var parameters = mapper.toPersonQueryParameters(request);
    var page = service.findAll(parameters);
    var response = mapper.toPersonPageResponse(page);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  /// Returns a single person by primary key, or `NOT_FOUND` if absent.
  @Override
  @RunOnVirtualThread
  public void findById(FindRequest request, StreamObserver<PersonResponse> responseObserver) {
    var person = service.findById(request.getId(), request.getAppend()).orElse(null);
    if (person == null) {
      responseObserver.onError(Status.NOT_FOUND
          .withDescription("Movie not found with id: " + request.getId())
          .asRuntimeException());
      return;
    }
    var response = mapper.toPersonResponse(person);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  /// Updates an existing person by primary key.  
  @Override
  @RunOnVirtualThread
  public void update(UpdatePersonRequest request, StreamObserver<PersonResponse> responseObserver) {
    var command = mapper.toUpdatePerson(request.getCommand());
    var person = service.update(request.getId(), command);
    var response = mapper.toPersonResponse(person);
    responseObserver.onNext(response);
    responseObserver.onCompleted();    
  }
  
  /// Deletes a person by primary key.
  @Override
  @RunOnVirtualThread
  public void delete(DeleteRequest request, StreamObserver<Empty> responseObserver) {
    service.delete(request.getId()); 
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
