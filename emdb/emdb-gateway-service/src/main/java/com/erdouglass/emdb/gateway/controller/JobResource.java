package com.erdouglass.emdb.gateway.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestStreamElementType;

import com.erdouglass.emdb.common.message.JobMessage;
import com.erdouglass.emdb.gateway.client.JobClient;

import io.smallrye.mutiny.Multi;

@Path("/jobs")
public class JobResource {
  
  @Inject
  @RestClient
  JobClient client;
  
  @GET 
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<JobMessage> findAll() {
    return client.findAll(); 
  }
  
  @GET
  @Path("/{id}")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<JobMessage> findById(@PathParam("id") String id) {
    return client.findById(id);
  }

}
