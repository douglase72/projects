package com.erdouglass.emdb.media.person.adapter.in.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.person.SavePersonUseCase;
import com.erdouglass.emdb.media.person.domain.exception.LockedPersonException;

@Path("/people")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class PersonResource {
  
  @Inject
  CommandMapper mapper;
  
  @Inject
  SavePersonUseCase saveUseCase;
  
  @Inject
  UriInfo uriInfo;

  /// Ingests a person keyed by its TMDB id, creating it if it is new.
  ///
  /// Idempotent by construction: replaying the same body leaves the catalogue in
  /// the same state, and the second call reports `UNCHANGED` rather than writing
  /// again. That is what makes the endpoint safe for a feed to retry.
  ///
  /// The body replaces the person wholesale — an omitted optional field is
  /// cleared, not preserved.
  ///
  /// @param tmdbId the natural id from the path, must be positive
  /// @param request the complete intended state of the person
  /// @return `201` with a `Location` header when the person was created, `200`
  ///         otherwise; the body carries the result either way
  /// @throws LockedPersonException if the person exists and is locked, mapped to
  ///         `423`
  @PUT
  @Path("/tmdb/{tmdbId}")
  public Response save(
      @NotNull @Positive @PathParam("tmdbId") Integer tmdbId,
      @NotNull @Valid SavePersonRequest request) {
    var command = mapper.toSavePersonCommand(TmdbId.of(tmdbId), request);
    var result = saveUseCase.save(command);
    return switch (result.status()) {
      case CREATED -> Response
          .created(uriInfo.getBaseUriBuilder().path(PersonResource.class).path(result.id()).build())
          .entity(result)
          .build();
      case UPDATED, UNCHANGED -> Response.ok(result).build();
    };
  }
}
