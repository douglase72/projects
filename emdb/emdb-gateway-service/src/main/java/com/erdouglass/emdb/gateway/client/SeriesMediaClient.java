package com.erdouglass.emdb.gateway.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.erdouglass.emdb.common.command.SeriesUpdateCommand;
import com.erdouglass.emdb.common.query.SeriesDto;

@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SeriesMediaClient {

  @GET
  @Path("{id}")
  public SeriesDto findById(@PathParam("id") Long id);
  
  @PATCH
  @Path("{id}")
  public SeriesDto update(@PathParam("id") Long id, SeriesUpdateCommand request);
  
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") Long id);
  
}
