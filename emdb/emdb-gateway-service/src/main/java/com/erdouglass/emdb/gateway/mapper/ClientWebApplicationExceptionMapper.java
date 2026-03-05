package com.erdouglass.emdb.gateway.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.reactive.ClientWebApplicationException;

@Provider
public class ClientWebApplicationExceptionMapper implements ExceptionMapper<ClientWebApplicationException> {
  
  @Override
  public Response toResponse(ClientWebApplicationException exception) {
    var response = exception.getResponse();
    var body = response.readEntity(String.class);
    return Response.status(response.getStatus())
        .entity(body)
        .type(response.getMediaType())
        .build();    
  }
  
}
