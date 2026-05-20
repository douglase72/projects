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

/// REST endpoint for triggering an ingest [Scheduler] on demand.
///
/// Schedulers normally run on their cron trigger; this endpoint exists for
/// operational and development use — for example, kicking off a pass after
/// a deployment or while debugging a missed window. Each request is
/// dispatched to the scheduler matching the supplied [MediaType].
@Path("/scheduler")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class SchedulerResource {
  
  @Inject
  Scheduler movieScheduler;
  
  /// Runs a single pass of the scheduler matching the command's media type.
  ///
  /// Execution is fire-and-forget from the caller's perspective: this
  /// method returns once the scheduler has been dispatched, not once the
  /// resulting work has completed. The scheduler's own concurrency
  /// control (e.g., `ConcurrentExecution.SKIP`) governs what happens if a
  /// run is already in progress.
  ///
  /// @param command identifies which scheduler to run
  /// @return `202 Accepted` once the scheduler has been dispatched
  /// @throws BadRequestException if no scheduler is registered for the
  ///                             requested media type  
  @POST
  public Response execute(@NotNull @Valid ExecuteScheduler command) {
    switch (command.type()) {
      case MOVIE -> movieScheduler.execute();
      default -> throw new BadRequestException("not yet supported");
    } 
    return Response.status(Status.ACCEPTED).build();
  }
}
