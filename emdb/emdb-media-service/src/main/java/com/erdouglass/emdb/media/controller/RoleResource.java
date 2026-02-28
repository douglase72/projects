package com.erdouglass.emdb.media.controller;

import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.common.comand.UpdateRole;
import com.erdouglass.emdb.common.query.RoleDto;
import com.erdouglass.emdb.media.service.RoleService;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleResource {
  
  @Inject
  RoleService service;
  
  @PUT
  @Path("/{id}")
  public RoleDto update(
      @PathParam("id") @NotNull UUID id, 
      @NotNull @Valid UpdateRole command) {
    return service.update(id, command);
  }

}
