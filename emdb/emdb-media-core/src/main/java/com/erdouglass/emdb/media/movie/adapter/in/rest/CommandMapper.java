package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.util.UUID;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

final class CommandMapper {
  
  private CommandMapper() { }
  
  public static SaveMovieCommand toSaveMovieCommand(Integer tmdbId, SaveMovieRequest request) {
    var details = MovieDetails.builder()
        .title(Title.of(request.title()))
        .releaseDate(request.releaseDate() != null ? ReleaseDate.from(request.releaseDate()) : null)
        .score(request.score() != null ? Score.of(request.score()) : null)
        .originalLanguage(request.originalLanguage() != null ? LanguageCode.of(request.originalLanguage()) : null)
        .overview(request.overview() != null ? Overview.of(request.overview()) : null)        
        .build();    
    return SaveMovieCommand.of(TmdbId.of(tmdbId), details);
  }
  
  public static UpdateMovieCommand totoUpdateMovieCommand(UUID id, UpdateMovieRequest request) {
    var details = MovieDetails.builder()
        .title(Title.of(request.title()))
        .releaseDate(request.releaseDate() != null ? ReleaseDate.from(request.releaseDate()) : null)
        .score(request.score() != null ? Score.of(request.score()) : null)
        .originalLanguage(request.originalLanguage() != null ? LanguageCode.of(request.originalLanguage()) : null)
        .overview(request.overview() != null ? Overview.of(request.overview()) : null)        
        .build(); 
    return UpdateMovieCommand.of(PublicId.of(id), Version.of(request.version()), details);
  }
}
