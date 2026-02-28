package com.erdouglass.emdb.gateway.controller;

import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.comand.UpdateRole;
import com.erdouglass.emdb.common.query.RoleDto;
import com.erdouglass.emdb.gateway.client.RoleClient;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleResource {
  
  @Inject
  @RestClient
  RoleClient client;
  
  @PUT
  @Path("/{id}")
  public RoleDto update(@PathParam("id") UUID id, UpdateRole command) {
    return client.update(id, command);
  }

}
