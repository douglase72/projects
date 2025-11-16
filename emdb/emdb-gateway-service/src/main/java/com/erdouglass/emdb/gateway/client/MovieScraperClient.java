package com.erdouglass.emdb.gateway.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.erdouglass.emdb.common.command.IngestRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MovieScraperClient {
  
  @POST
  @Path("/ingest")
  public Response ingest(IngestRequest request);

}
