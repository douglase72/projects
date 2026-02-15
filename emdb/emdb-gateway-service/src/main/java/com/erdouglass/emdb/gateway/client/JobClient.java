package com.erdouglass.emdb.gateway.client;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;

@RegisterRestClient()
@Produces(MediaType.APPLICATION_JSON) 
public interface JobClient {

  @GET
  public Uni<List<Object>> findAll();
  
}
