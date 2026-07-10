package com.erdouglass.emdb.media.adapter.inbound.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.media.MediaFacade;
import com.erdouglass.emdb.media.SaveMovie;
import com.erdouglass.emdb.media.SavePerson;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveSeries;

@Path("/media")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MediaResource {
  
  @Inject
  MediaFacade facade;
  
  @POST
  @Path("/movies")
  public SaveResult saveMovie(SaveMovie command) {
    return facade.saveMovie(command);
  }
  
  @POST
  @Path("/people")
  public SaveResult savePerson(SavePerson command) {
    return facade.savePerson(command);
  }
  
  @POST
  @Path("/series")
  public SaveResult saveSeries(SaveSeries command) {
    return facade.saveSeries(command);
  }  
}
