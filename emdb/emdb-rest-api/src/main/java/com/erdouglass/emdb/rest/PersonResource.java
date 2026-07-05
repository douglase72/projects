package com.erdouglass.emdb.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.media.person.PersonCommandService;
import com.erdouglass.emdb.media.person.PersonDto;
import com.erdouglass.emdb.media.person.SavePerson;

@Path("/people")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

  @Inject
  PersonCommandService service;
  
  @POST
  public PersonDto save(SavePerson command) {
    return service.save(command);
  }  
}
