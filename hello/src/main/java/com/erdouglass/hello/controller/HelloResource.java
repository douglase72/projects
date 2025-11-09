package com.erdouglass.hello.controller;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
@Produces(MediaType.TEXT_PLAIN)
public class HelloResource {
	private static final Logger LOGGER = Logger.getLogger(HelloResource.class);
	
  @ConfigProperty(name = "hello.message")
  String message;
	
	@GET
	public String hello() {
		LOGGER.debugf("Message: %s", message);
		return message;
	}
	
}
