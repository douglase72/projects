package com.erdouglass.emdb.media.controller;

import org.jboss.logging.Logger;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {
	private static final Logger LOGGER = Logger.getLogger(MovieResource.class);
	
  @GET
  @Path("{id}")
  public String findById() {
  	var movie = "Austin Powers in Goldmember";
  	LOGGER.infof("Found: %s", movie);
  	return movie;
  }

}
