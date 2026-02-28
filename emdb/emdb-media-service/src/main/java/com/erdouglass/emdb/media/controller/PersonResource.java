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
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.EmdbResponse;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.media.dto.PersonStatus;
import com.erdouglass.emdb.media.service.PersonService;
import com.erdouglass.webservices.ResponseStatus;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {
  
  @Inject
  PersonService service;
  
  @POST
  public PersonDto save(@NotNull @Valid SavePerson command) {
    return service.save(command);
  }
  
  @POST
  @Path("/batch")
  public Response saveAll(@NotEmpty List<@Valid SavePerson> commands) {
    var results = service.saveAll(commands).stream()
        .map(this::toResponse)
        .toList();    
    return Response.status(ResponseStatus.MULTI_STATUS).entity(results).build();
  }  
  
  @GET
  @Path("/{id}")
  public PersonDto findById(
      @PathParam("id") @NotNull @Positive Long id,
      @QueryParam(Configuration.APPEND) String append) {
    return service.findById(id, append);
  }
  
  @PUT
  @Path("/{id}")
  public PersonDto update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid UpdatePerson command) {
    return service.update(id, command);
  }
  
  @DELETE
  @Path("/{id}")
  public Response deleteById(@PathParam("id") @NotNull @Positive Long id) {
    service.deleteById(id);
    return Response.noContent().build();
  }
  
  private EmdbResponse toResponse(PersonStatus personStatus) {
    var person = personStatus.person();
    var code = switch (personStatus.status()) {
      case CREATED -> Response.Status.CREATED;
      case UPDATED -> Response.Status.OK;
      case UNCHANGED -> Response.Status.NO_CONTENT;
      default -> throw new IllegalArgumentException("Invalid status: " + personStatus.status());
    };
    return EmdbResponse.of(person.id(), person.tmdbId(), code);
  }  

}
