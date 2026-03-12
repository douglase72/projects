package com.erdouglass.emdb.gateway.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.gateway.mapper.PersonMapper;
import com.erdouglass.emdb.media.proto.v1.PersonService;

import io.quarkus.grpc.GrpcClient;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {
  
  @Inject
  PersonMapper mapper;
  
  @GrpcClient("media-service")
  PersonService service;
  
  @POST
  public PersonDto save(SavePerson command) {
    var request = mapper.toSavePersonRequest(command);
    var response = service.save(request).await().indefinitely();
    return mapper.toPersonDto(response);
  }  

}
