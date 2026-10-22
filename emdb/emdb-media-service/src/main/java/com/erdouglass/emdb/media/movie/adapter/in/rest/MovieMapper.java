package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.util.UUID;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;
import com.erdouglass.emdb.media.shared.domain.model.LanguageCode;
import com.erdouglass.emdb.media.shared.domain.model.MediaId;
import com.erdouglass.emdb.media.shared.domain.model.Overview;
import com.erdouglass.emdb.media.shared.domain.model.Score;
import com.erdouglass.emdb.media.shared.domain.model.Title;
import com.erdouglass.emdb.media.shared.domain.model.Version;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

final class MovieMapper {
  
  private MovieMapper() { }
  
  public static SaveMovieCommand toSaveMovieCommand(Integer tmdbId, SaveMovieRequest request) {
    var details = MovieDetails.builder()
        .title(Title.of(request.title()))
        .releaseDate(request.releaseDate() != null ? ReleaseDate.from(request.releaseDate()) : null)
        .score(request.score() != null ? Score.of(request.score()) : null)
        .originalLanguage(request.originalLanguage() != null ? LanguageCode.of(request.originalLanguage()) : null)
        .overview(request.overview() != null ? Overview.of(request.overview()) : null)        
        .build();
    return SaveMovieCommand.of(
        MessageId.newId(), CorrelationId.of(request.correlationId().value()), TmdbId.of(tmdbId), details);
  }
  
  public static UpdateMovieCommand totoUpdateMovieCommand(UUID id, UpdateMovieRequest request) {
    var details = MovieDetails.builder()
        .title(Title.of(request.title()))
        .releaseDate(request.releaseDate() != null ? ReleaseDate.from(request.releaseDate()) : null)
        .score(request.score() != null ? Score.of(request.score()) : null)
        .originalLanguage(request.originalLanguage() != null ? LanguageCode.of(request.originalLanguage()) : null)
        .overview(request.overview() != null ? Overview.of(request.overview()) : null)        
        .build(); 
    return UpdateMovieCommand.of(MediaId.of(id), Version.of(request.version()), details);
  }
}
