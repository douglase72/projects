package com.erdouglass.emdb.media.controller;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.erdouglass.emdb.common.PersonCreateDto;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.common.query.ResponseDto;
import com.erdouglass.emdb.common.request.PersonUpdateRequest;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.query.PersonStatus;
import com.erdouglass.emdb.media.service.PersonService;
import com.erdouglass.webservices.ResponseStatus;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {
  public static final String APPEND = "append";
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonService service;
  
  @POST
  public Response create(@NotNull @Valid PersonCreateDto request) {
    var person = service.create(mapper.toPerson(request));
    var location = UriBuilder.fromResource(MovieResource.class)
        .path("/{id}")
        .resolveTemplate("id", person.id())
        .build();
    return Response.created(location).entity(mapper.toPersonDto(person)).build();
  }
  
  @POST
  @Path("/batch")
  public Response createAll(@NotEmpty List<@Valid PersonCreateDto> requests) {
    var response = service.createAll(requests.stream().map(mapper::toPerson).toList())
        .stream()
        .map(this::toResponseDto)
        .toList();
    return Response.status(ResponseStatus.MULTI_STATUS).entity(response).build();
  }
  
  @GET
  @Path("/{id}")
  public PersonDto findById(
      @PathParam("id") @NotNull @Positive Long id,
      @QueryParam(APPEND) String append) {
    return mapper.toPersonDto(service.findById(id, append));
  }
  
  @PATCH
  @Path("/{id}")
  public PersonDto update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid PersonUpdateRequest request) {
    var movie = service.update(id, request);
    return mapper.toPersonDto(movie);
  }
  
  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    service.delete(id);
    return Response.noContent().build();
  }
  
  private ResponseDto toResponseDto(PersonStatus result) {
    var person = result.person();
    var code = switch (result.statusCode()) {
      case CREATED -> Response.Status.CREATED.getStatusCode();
      case UNCHANGED -> Response.Status.OK.getStatusCode();
      default -> throw new IllegalArgumentException("Invalid status: " + result.statusCode());
    };
    return new ResponseDto(person.id(), person.tmdbId(), code);
  }

}
