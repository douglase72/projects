package com.erdouglass.emdb.gateway.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestStreamElementType;

import com.erdouglass.emdb.common.message.JobMessage;

import io.smallrye.mutiny.Multi;

@RegisterRestClient()
public interface JobClient {
  
  @GET 
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<JobMessage> findAll();

  @GET
  @Path("{id}")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  Multi<JobMessage> findById(@PathParam("id") String jobId);
  
}
