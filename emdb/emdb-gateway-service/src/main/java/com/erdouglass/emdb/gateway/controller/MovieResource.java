package com.erdouglass.emdb.gateway.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.gateway.mapper.MovieMapper;
import com.erdouglass.emdb.media.proto.v1.MovieService;

import io.quarkus.grpc.GrpcClient;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  MovieMapper mapper;
  
  @GrpcClient("media-service")
  MovieService service;
  
  @POST
  public Response save(SaveMovie command) {
    var request = mapper.toSaveMovieRequest(command);    
    var response = mapper.toMovieDto(service.save(request).await().indefinitely());    
    return Response.ok(response).build();
  }
  
}
