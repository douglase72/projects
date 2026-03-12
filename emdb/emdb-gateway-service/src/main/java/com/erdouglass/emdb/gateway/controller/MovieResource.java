package com.erdouglass.emdb.gateway.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.query.MovieDto;
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
  public MovieDto save(SaveMovie command) {
    var request = mapper.toSaveMovieRequest(command);
    var response = service.save(request).await().indefinitely();
    return mapper.toMovieDto(response);
  }
  
  @GET
  @Path("/{id}")
  public MovieDto findById(@PathParam("id") Long id, @QueryParam(Configuration.APPEND) String append) {
    var response = service.findById(mapper.toFindMovieRequest(id, append)).await().indefinitely();
    return mapper.toMovieDto(response);
  } 
  
}
