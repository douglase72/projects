package com.erdouglass.emdb.gateway.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.query.SeriesDto;

@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SeriesClient {

  @POST
  public SeriesDto save(SaveSeries command);
  
  @GET
  @Path("/{id}")
  public SeriesDto findById(@PathParam("id") Long id, @QueryParam(Configuration.APPEND) String append);
  
  @PUT
  @Path("/{id}")
  public SeriesDto update(@PathParam("id") Long id, UpdateSeries command);
  
  @DELETE
  @Path("/{id}")
  public Response deleteById(@PathParam("id") Long id);
  
}
