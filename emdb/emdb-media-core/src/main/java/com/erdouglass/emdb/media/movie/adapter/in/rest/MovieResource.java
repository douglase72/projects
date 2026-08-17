package com.erdouglass.emdb.media.movie.adapter.in.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.DeleteMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.LockMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.LockMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieUseCase;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;

/// REST write surface for the movie catalogue.
///
/// Every endpoint here mutates; reads are served by the GraphQL resolver. The
/// resource maps request bodies to commands, delegates to a use case, and
/// translates the outcome into a status code — it holds no rules of its own.
///
/// Two paths lead to a write, and they differ in how they identify the target
/// and whether they check staleness:
///
/// * `PUT /movies/tmdb/{tmdbId}` — ingestion. Keyed by natural id, no version,
///   creates on first sight.
/// * `PUT /movies/{id}` — editing. Keyed by catalogue id, version required,
///   fails if the title has moved on.
///
/// Domain failures are not caught here. Exception mappers translate a missing
/// title to `404`, a version mismatch to `409` and a locked title to `423`, so
/// each method reads as its happy path.
///
/// Package-private: the class is a JAX-RS endpoint, discovered by the runtime
/// rather than referenced by other code.
@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MovieResource {
  
  @Inject
  SaveMovieUseCase saveUseCase;
  
  @Inject
  UpdateMovieUseCase updateUseCase;
  
  @Inject
  LockMovieUseCase lockUseCase;
  
  @Inject
  DeleteMovieUseCase deleteUseCase;
  
  @Inject
  CommandMapper mapper;
  
  @Inject
  UriInfo uriInfo;

  /// Ingests a title keyed by its TMDB id, creating it if it is new.
  ///
  /// Idempotent by construction: replaying the same body leaves the catalogue in
  /// the same state, and the second call reports `UNCHANGED` rather than writing
  /// again. That is what makes the endpoint safe for a feed to retry.
  ///
  /// The body replaces the title wholesale — an omitted optional field is
  /// cleared, not preserved.
  ///
  /// @param tmdbId the natural id from the path, must be positive
  /// @param request the complete intended state of the title
  /// @return `201` with a `Location` header when the title was created, `200`
  ///         otherwise; the body carries the result either way
  /// @throws LockedPersonException if the title exists and is locked, mapped to
  ///         `423`
  @PUT
  @Path("/tmdb/{tmdbId}")
  public Response save(
      @NotNull @Positive @PathParam("tmdbId") Integer tmdbId,
      @NotNull @Valid SaveMovieRequest request) {
    var command = mapper.toSaveMovieCommand(TmdbId.of(tmdbId), request);
    var result = saveUseCase.save(command);
    return switch (result.status()) {
      case CREATED -> Response
          .created(uriInfo.getBaseUriBuilder().path(MovieResource.class).path(result.id()).build())
          .entity(result)
          .build();
      case UPDATED, UNCHANGED -> Response.ok(result).build();
    };
  }
  
  /// Edits an existing title, refusing the write if the client's version is
  /// stale.
  ///
  /// Never creates: a catalogue id that matches no title is a `404`, since the
  /// client is claiming to edit something it read.
  ///
  /// @param id the catalogue id from the path, e.g. `mv_42`
  /// @param request the complete intended state plus the version the client read
  /// @return the outcome and the version to hold going forward
  /// @throws MovieNotFoundException if no title carries `id`, mapped to `404`
  /// @throws StalePersonException if the stored version differs from the one
  ///         supplied, mapped to `409`
  /// @throws LockedPersonException if the title is locked, mapped to `423`
  @PUT
  @Path("/{id}")
  public SaveResult update(
      @NotBlank @PathParam("id") String id, 
      @NotNull @Valid UpdateMovieRequest request) {
    var command = mapper.toUpdateMovieCommand(id, request);
    return updateUseCase.update(command);
  }
  
  /// Locks or unlocks a title against detail changes.
  ///
  /// Version-checked like an edit, and reported as an update, because it is one:
  /// the write bumps the version, and a client holding the old one must re-read.
  ///
  /// Unlike the other write paths, this one succeeds on an already-locked title
  /// — otherwise a lock could never be lifted.
  ///
  /// @param id the catalogue id from the path
  /// @param request the desired lock state and the version the client read
  /// @return the outcome and the version after the write
  /// @throws MovieNotFoundException if no title carries `id`, mapped to `404`
  /// @throws StalePersonException if the version is stale, mapped to `409`
  @PUT
  @Path("/lock/{id}")
  public SaveResult lock(
      @NotBlank @PathParam("id") String id, 
      @NotNull @Valid LockMovieRequest request) {
    var command = new LockMovieCommand(MoviePublicId.of(id), Version.of(request.version()), request.lock());
    return lockUseCase.lock(command);
  }
  
  /// Removes a title from the catalogue.
  ///
  /// Not version-checked, so a delete cannot be refused as stale. Before the row
  /// goes, the audit trail records every populated field as removed, so the
  /// title's history survives the title.
  ///
  /// @param id the catalogue id from the path
  /// @return `204` with no body
  /// @throws MovieNotFoundException if no title carries `id`, mapped to `404`
  @DELETE
  @Path("/{id}")
  public Response delete(@NotBlank  @PathParam("id") String id) {
    deleteUseCase.delete(MoviePublicId.of(id));
    return Response.noContent().build();
  }
}
