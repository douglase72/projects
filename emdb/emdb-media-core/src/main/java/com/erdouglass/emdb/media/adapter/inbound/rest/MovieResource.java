package com.erdouglass.emdb.media.adapter.inbound.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieUseCase;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.application.port.inbound.UpdateMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.UpdateResult;

/// Driving adapter: translates HTTP into invocations of the write-side ports.
///
/// This class sits *outside* the hexagon. Its sole job is dialect translation:
/// JSON and Bean Validation on the way in, status codes and the `Location`
/// header on the way out. It is the only place on the write path where HTTP
/// vocabulary may appear — and conversely, no HTTP type may escape it.
///
/// Boundary rules:
///   - depends only on inbound ports ([SaveMovieUseCase]) and their
///     command/result records — never on the application service, the domain
///     model, or anything in `adapter.outbound`
///   - holds no business rules; validation annotations here are transport
///     hygiene, re-enforced authoritatively by the domain's value objects
///   - package-private, so the adapter can never become anyone's dependency
///
/// Status mapping is the adapter's interpretation of the port-level fact
/// [SaveResult.Status]: `CREATED` → 201 + `Location`, `UPDATED` → 200.
@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MovieResource {

  @Inject
  SaveMovieUseCase saveUseCase;
  
  @Inject
  UpdateMovieUseCase updateUseCase;
  
  @POST
  public Response save(@NotNull @Valid SaveMovieCommand command, @Context UriInfo uriInfo) {
    var result = saveUseCase.save(command);
    return switch (result.status()) {
      case CREATED -> Response
          .created(uriInfo.getAbsolutePathBuilder().path(result.id()).build())
          .entity(result)
          .build();
      case UPDATED -> Response.ok(result).build();
    };
  }
  
  @PUT
  @Path("/{id}")
  public UpdateResult update(
      @NotBlank @PathParam("id") String id, @NotNull @Valid UpdateMovieCommand command) {
    return updateUseCase.update(id, command);    
  }
}
