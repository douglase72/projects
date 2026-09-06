package com.erdouglass.emdb.media.movie.adapter.in.rest;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovie;
import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

final class CommandMapper {
  
  private CommandMapper() { }
  
  public static SaveMovieCommand toSaveMovieCommand(Integer tmdbId, SaveMovieRequest request) {
    return SaveMovie.builder()
        .tmdbId(TmdbId.of(tmdbId))
        .title(Title.of(request.title()))
        .releaseDate(request.releaseDate() != null ? ReleaseDate.from(request.releaseDate()) : null)
        .score(request.score() != null ? Score.of(request.score()) : null)
        .originalLanguage(request.originalLanguage() != null ? LanguageCode.of(request.originalLanguage()) : null)
        .overview(request.overview() != null ? Overview.of(request.overview()) : null)
        .build();
  }
}
