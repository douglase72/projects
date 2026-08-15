package com.erdouglass.emdb.media.adapter.inbound.movie;

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

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.application.port.inbound.movie.DeleteMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.movie.LockMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.movie.LockMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.movie.SaveMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.movie.SaveMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.movie.SaveResult;
import com.erdouglass.emdb.media.application.port.inbound.movie.UpdateMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.movie.UpdateMovieUseCase;
import com.erdouglass.emdb.media.domain.movie.MovieDetails;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.Title;
import com.erdouglass.emdb.media.domain.shared.LanguageCode;
import com.erdouglass.emdb.media.domain.shared.Overview;
import com.erdouglass.emdb.media.domain.shared.Score;
import com.erdouglass.emdb.media.domain.shared.Version;

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
  UriInfo uriInfo;

  @PUT
  @Path("/tmdb/{tmdbId}")
  public Response save(
      @NotNull @Positive @PathParam("tmdbId") Integer tmdbId,
      @NotNull @Valid SaveMovieRequest request) {
    var details = MovieDetails.builder()
        .title(Title.of(request.title()))
        .releaseDate(request.releaseDate().map(r -> ReleaseDate.from(r)).orElse(null))
        .score(request.score().map(s -> Score.of(s)).orElse(null))
        .originalLanguage(request.originalLanguage().map(l -> LanguageCode.of(l)).orElse(null))
        .overview(request.overview().map(o -> Overview.of(o)).orElse(null))
        .build();
    var command = SaveMovieCommand.of(TmdbId.of(tmdbId), details);
    var result = saveUseCase.save(command);
    return switch (result.status()) {
      case CREATED -> Response
          .created(uriInfo.getBaseUriBuilder().path(MovieResource.class).path(result.id()).build())
          .entity(result)
          .build();
      case UPDATED, UNCHANGED -> Response.ok(result).build();
    };
  }
  
  @PUT
  @Path("/{id}")
  public SaveResult update(
      @NotBlank @PathParam("id") String id, 
      @NotNull @Valid UpdateMovieRequest request) {
    var details = MovieDetails.builder()
        .title(Title.of(request.title()))
        .releaseDate(request.releaseDate().map(r -> ReleaseDate.from(r)).orElse(null))
        .score(request.score().map(s -> Score.of(s)).orElse(null))
        .originalLanguage(request.originalLanguage().map(l -> LanguageCode.of(l)).orElse(null))
        .overview(request.overview().map(o -> Overview.of(o)).orElse(null))
        .build();
    var command = new UpdateMovieCommand(MoviePublicId.of(id), Version.of(request.version()), details);
    return updateUseCase.update(command);
  }
  
  @PUT
  @Path("/lock/{id}")
  public SaveResult lock(
      @NotBlank @PathParam("id") String id, 
      @NotNull @Valid LockMovieRequest request) {
    var command = new LockMovieCommand(MoviePublicId.of(id), Version.of(request.version()), request.lock());
    return lockUseCase.lock(command);
  }
  
  @DELETE
  @Path("/{id}")
  public Response delete(@NotBlank  @PathParam("id") String id) {
    deleteUseCase.delete(MoviePublicId.of(id));
    return Response.noContent().build();
  }
}
