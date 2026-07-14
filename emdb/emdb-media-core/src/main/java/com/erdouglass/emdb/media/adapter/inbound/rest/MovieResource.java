package com.erdouglass.emdb.media.adapter.inbound.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.erdouglass.emdb.media.SaveMovie;
import com.erdouglass.emdb.media.SaveMovieUseCase;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.application.port.inbound.movie.DeleteMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.movie.UpdateMovie;
import com.erdouglass.emdb.media.application.port.inbound.movie.UpdateMovieUseCase;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {
  
  @Inject
  DeleteMovieUseCase deleteUseCase;

  @Inject
  SaveMovieUseCase saveUseCase;
  
  @Inject
  UpdateMovieUseCase updateUseCase;
  
  @POST
  public SaveResult save(SaveMovie command) {
    return saveUseCase.save(command);
  }
  
  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") Long id, UpdateMovie command) {
    updateUseCase.update(id, command);
    return Response.noContent().build();
  }
  
  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") Long id) {
    deleteUseCase.deleteById(id);
    return Response.noContent().build();
  }
}
