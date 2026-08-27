package com.erdouglass.emdb.media.movie.adapter.in.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.erdouglass.emdb.media.kernel.SourceId;
import com.erdouglass.emdb.media.kernel.SourceId.Source;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;

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
  CommandMapper mapper;
  
  @Inject
  UriInfo uriInfo;

  /// Ingests a title keyed by its source id, creating it if it is new.
  ///
  /// Idempotent by construction: replaying the same body leaves the catalogue in
  /// the same state, and the second call reports `UNCHANGED` rather than writing
  /// again. That is what makes the endpoint safe for a feed to retry.
  ///
  /// The body replaces the title wholesale — an omitted optional field is
  /// cleared, not preserved.
  ///
  /// @param sourceId the natural id from the path, must be positive
  /// @param request the complete intended state of the title
  /// @return `201` with a `Location` header when the title was created, `200`
  ///         otherwise; the body carries the result either way
  @PUT
  @Path("/{source}/{id}")
  public Response save(
      @NotBlank @PathParam("source") String source,
      @NotBlank @PathParam("id") String id,
      @NotNull @Valid SaveMovieRequest request) {
    var command = mapper.toSaveMovieCommand(SourceId.of(Source.from(source), id), request);
    var result = saveUseCase.save(command);
    return switch (result.status()) {
      case CREATED -> Response
          .created(uriInfo.getBaseUriBuilder().path(MovieResource.class).path(result.id()).build())
          .entity(result)
          .build();
      case UPDATED, UNCHANGED -> Response.ok(result).build();
    };
  }
}
