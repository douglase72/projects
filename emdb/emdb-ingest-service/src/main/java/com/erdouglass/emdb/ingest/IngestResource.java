package com.erdouglass.emdb.ingest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com.erdouglass.emdb.ingest.movie.MovieScheduler;

@Path("/ingest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IngestResource {
  
  @Inject
  MovieScheduler movieScheduler;
  
  @POST
  public Response execute(@NotNull @Valid ExecuteScheduler command) {
    movieScheduler.execute();
    return Response.status(Status.ACCEPTED).build();
  }
}
