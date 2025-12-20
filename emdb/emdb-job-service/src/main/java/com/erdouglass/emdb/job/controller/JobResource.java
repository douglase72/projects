package com.erdouglass.emdb.job.controller;

import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestStreamElementType;

import com.erdouglass.emdb.common.message.JobMessage;
import com.erdouglass.emdb.job.service.JobService;

import io.smallrye.mutiny.Multi;

@Path("/jobs")
public class JobResource {

  @Inject
  JobService service;
  
  @GET
  @Path("{id}")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<JobMessage> streamJob(@PathParam("id") UUID id) {
      return service.stream(id);
  }
  
}
