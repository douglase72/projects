package com.erdouglass.emdb.ingest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/// JAX-RS resource for manually triggering ingest schedulers. Intended for
/// operational use (CLI, ops dashboard) when an out-of-band ingest run is
/// needed without waiting for the next cron tick.
@Path("/ingest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IngestResource {
  
  @Inject
  Scheduler movieScheduler;
  
  /// Triggers an immediate run of the scheduler selected by the command's
  /// [MediaType].
  ///
  /// Returns `202 Accepted` rather than `200 OK` because the scheduler runs
  /// asynchronously — the response confirms the request was accepted, not
  /// that ingest has completed.
  ///
  /// @param command the validated execute-scheduler payload
  /// @return `202 Accepted` with no body  
  @POST
  public Response execute(@NotNull @Valid ExecuteScheduler command) {
    switch (command.type()) {
      case MOVIE -> movieScheduler.execute();
      default -> throw new BadRequestException("not yet supported");
    }
    return Response.status(Status.ACCEPTED).build();
  }
}
