package com.erdouglass.emdb.gateway.client;

import java.util.UUID;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.erdouglass.emdb.common.comand.UpdateRole;
import com.erdouglass.emdb.common.query.RoleDto;

@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RoleClient {

  @PUT
  @Path("/{id}")
  public RoleDto update(@PathParam("id") UUID id, UpdateRole command);
  
}
