package com.erdouglass.emdb.media.adapter.inbound.rest.movie;

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

import com.erdouglass.emdb.media.LanguageCode;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.Score;
import com.erdouglass.emdb.media.Title;
import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.application.port.inbound.movie.DeleteMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.movie.UpdateMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.movie.UpdateMovieUseCase;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.shared.Version;
import com.erdouglass.emdb.media.movie.ReleaseDate;
import com.erdouglass.emdb.media.movie.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.SaveMovieUseCase;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MovieResource {
  
  @Inject
  SaveMovieUseCase saveUseCase;
  
  @Inject
  UpdateMovieUseCase updateUseCase;
  
  @Inject
  DeleteMovieUseCase deleteUseCase;
  
  @Inject
  UriInfo uriInfo;

  @PUT
  @Path("/tmdb/{tmdbId}")
  public Response save(
      @NotNull @Positive @PathParam("tmdbId") Integer tmdbId,
      @NotNull @Valid SaveMovieRequest request) {
    var command = SaveMovieCommand.builder()
        .tmdbId(TmdbId.of(tmdbId))
        .title(Title.of(request.title()))
        .releaseDate(request.releaseDate().map(r -> ReleaseDate.from(r)).orElse(null))
        .score(request.score().map(s -> Score.of(s)).orElse(null))
        .originalLanguage(request.originalLanguage().map(l ->  LanguageCode.of(l)).orElse(null))
        .build();
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
    var command = UpdateMovieCommand.builder()
        .publicId(MoviePublicId.of(id))
        .version(Version.of(request.version()))
        .title(Title.of(request.title()))
        .releaseDate(request.releaseDate().map(r -> ReleaseDate.from(r)).orElse(null))
        .score(request.score().map(s -> Score.of(s)).orElse(null))
        .originalLanguage(request.originalLanguage().map(l ->  LanguageCode.of(l)).orElse(null))
        .build();
    return updateUseCase.update(command);
  }
  
  @DELETE
  @Path("/{id}")
  public Response delete(@NotBlank  @PathParam("id") String id) {
    deleteUseCase.delete(MoviePublicId.of(id));
    return Response.noContent().build();
  }
}
