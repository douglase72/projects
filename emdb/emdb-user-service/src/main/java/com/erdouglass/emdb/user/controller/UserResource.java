package com.erdouglass.emdb.user.controller;

import java.util.UUID;

import jakarta.inject.Inject;

import com.erdouglass.emdb.user.api.Theme;
import com.erdouglass.emdb.user.entity.User;
import com.erdouglass.emdb.user.mapper.UserMapper;
import com.erdouglass.emdb.user.proto.v1.FindOrCreateRequest;
import com.erdouglass.emdb.user.proto.v1.UpdateUserRequest;
import com.erdouglass.emdb.user.proto.v1.UserResponse;
import com.erdouglass.emdb.user.proto.v1.UserServiceGrpc.UserServiceImplBase;
import com.erdouglass.emdb.user.service.UserService;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;

@GrpcService
public class UserResource extends UserServiceImplBase {
  
  @Inject
  UserMapper mapper;

  @Inject
  UserService service;
  
  @Override
  @RunOnVirtualThread
  public void findOrCreate(FindOrCreateRequest request, StreamObserver<UserResponse> responseObserver) {
    UUID id = UUID.fromString(request.getId());     
    var user = service.findById(id).orElseGet(() -> provision(request));
    var response = mapper.toUserResponse(user);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
  
  @Override
  @RunOnVirtualThread
  public void update(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
    var command = mapper.toUpdateUser(request);
    var movie = service.update(UUID.fromString(request.getId()), command);
    var response = mapper.toUserResponse(movie);
    responseObserver.onNext(response);
    responseObserver.onCompleted();        
  }
  
  private User provision(FindOrCreateRequest request) {
    var user = new User();
    user.setId(UUID.fromString(request.getId()));
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setTheme(Theme.LIGHT);
    return service.create(user);
  }
}
