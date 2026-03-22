package com.erdouglass.emdb.gateway.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.gateway.mapper.MovieMapper;
import com.erdouglass.emdb.media.proto.v1.DeleteMovieRequest;
import com.erdouglass.emdb.media.proto.v1.MovieServiceGrpc;

import io.quarkus.grpc.GrpcClient;

@Path("/movies")
@RolesAllowed(Configuration.ADMIN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  MovieMapper mapper;
  
  @GrpcClient("media-service")
  MovieServiceGrpc.MovieServiceBlockingStub service;
  
  @POST
  public Response save(@NotNull @Valid SaveMovie command) {
    var request = mapper.toSaveMovieRequest(command);
    var response = service.save(request);
    return Response.status(mapper.mapProtoStatusToHttpCode(response.getStatus()))
        .entity(mapper.toMovieDto(response.getMovie()))
        .build();
  }
  
  @PermitAll
  @GET
  @Path("/{id}")
  public MovieDto findById(
      @PathParam("id") @NotNull @Positive Long id, 
      @QueryParam(Configuration.APPEND) String append) {
    var response = service.findById(mapper.toFindMovieRequest(id, append));
    return mapper.toMovieDto(response);
  }
  
  @PUT
  @Path("/{id}")
  public MovieDto update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid UpdateMovie command) {
    var request = mapper.toUpdateMovieRequest(id, command);
    var response = service.update(request);
    return mapper.toMovieDto(response);
  }
  
  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    var request = DeleteMovieRequest.newBuilder().setId(id).build();
    service.delete(request);
    return Response.noContent().build();
  }
  
}
