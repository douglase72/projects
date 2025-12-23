package com.erdouglass.emdb.media.controller;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.common.MovieCreditCreateDto;
import com.erdouglass.emdb.common.query.ResponseDto;
import com.erdouglass.emdb.media.query.MovieCreditStatus;
import com.erdouglass.emdb.media.service.MovieCreditService;
import com.erdouglass.webservices.ResponseStatus;

@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieCreditResource {
  
  @Inject
  MovieCreditService service;
  
  @PUT
  public Response synchronizeAll(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotEmpty List<@Valid MovieCreditCreateDto> requests) {
    var response = service.synchronizeAll(id, requests).stream()
        .map(this::toResponseDto)
        .toList();
    return Response.status(ResponseStatus.MULTI_STATUS).entity(response).build();
  }
  
  private ResponseDto toResponseDto(MovieCreditStatus result) {
    var credit = result.credit();
    var code = switch (result.statusCode()) {
      case CREATED -> Response.Status.CREATED.getStatusCode();
      case UPDATED -> Response.Status.OK.getStatusCode();
      case UNCHANGED -> Response.Status.NOT_MODIFIED.getStatusCode();
      case DELETED -> Response.Status.NO_CONTENT.getStatusCode();
    };
    return new ResponseDto(credit.id(), credit.person().tmdbId(), code);
  }

}
