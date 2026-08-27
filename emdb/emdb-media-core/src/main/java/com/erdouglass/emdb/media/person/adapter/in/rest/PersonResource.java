package com.erdouglass.emdb.media.person.adapter.in.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.media.kernel.SourceId;
import com.erdouglass.emdb.media.kernel.SourceId.Source;
import com.erdouglass.emdb.media.movie.application.port.out.Result;
import com.erdouglass.emdb.media.person.application.port.in.DeletePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.in.UpdatePersonUseCase;
import com.erdouglass.emdb.media.person.domain.PersonPublicId;
import com.erdouglass.emdb.media.person.domain.exception.PersonNotFoundException;
import com.erdouglass.emdb.media.person.domain.exception.StalePersonException;

@Path("/people")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class PersonResource {
  
  @Inject
  CommandMapper mapper;
  
  @Inject
  SavePersonUseCase saveUseCase;
  
  @Inject
  UpdatePersonUseCase updateUseCase;
  
  @Inject
  DeletePersonUseCase deleteUseCase;
  
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
  @PUT
  @Path("/{source}/{id}")
  public Response save(
      @NotBlank @PathParam("source") String source,
      @NotBlank @PathParam("id") String id,
      @NotNull @Valid SavePersonRequest request) {
    var command = mapper.toSavePersonCommand(SourceId.of(Source.from(source), id), request);
    var result = saveUseCase.save(command);
    return switch (result.status()) {
      case CREATED -> Response
          .created(uriInfo.getBaseUriBuilder().path(PersonResource.class).path(result.id()).build())
          .entity(result)
          .build();
      case UPDATED, UNCHANGED -> Response.ok(result).build();
    };
  }
  
  /// Edits an existing person, refusing the write if the client's version is
  /// stale.
  ///
  /// Never creates: a catalogue id that matches no person is a `404`, since the
  /// client is claiming to edit something it read.
  ///
  /// @param id the catalogue id from the path, e.g. `pr_42`
  /// @param request the complete intended state plus the version the client read
  /// @return the outcome and the version to hold going forward
  /// @throws PersonNotFoundException if no title carries `id`, mapped to `404`
  /// @throws StalePersonException if the stored version differs from the one
  ///         supplied, mapped to `409`
  @PUT
  @Path("/{id}")
  public Result update(
      @NotBlank @PathParam("id") String id, 
      @NotNull @Valid UpdatePersonRequest request) {
    var command = mapper.toUpdatePersonCommand(id, request);
    return updateUseCase.update(command);
  }
  
  /// Removes a person from the catalogue.
  ///
  /// Not version-checked, so a delete cannot be refused as stale. Before the row
  /// goes, the audit trail records every populated field as removed, so the
  /// person's history survives the person.
  ///
  /// @param id the catalogue id from the path
  /// @return `204` with no body
  /// @throws PersonNotFoundException if no person carries `id`, mapped to `404`
  @DELETE
  @Path("/{id}")
  public Response delete(@NotBlank  @PathParam("id") String id) {
    deleteUseCase.delete(PersonPublicId.of(id));
    return Response.noContent().build();
  }
}
