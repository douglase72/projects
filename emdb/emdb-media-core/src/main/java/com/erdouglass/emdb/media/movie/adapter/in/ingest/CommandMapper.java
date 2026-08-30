package com.erdouglass.emdb.media.movie.adapter.in.ingest;

import java.util.List;

import com.erdouglass.emdb.media.api.LoadMovieCommand;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

final class CommandMapper {

  private CommandMapper() { }
  
  public static SaveMovieCommand toSaveMovieCommand(LoadMovieCommand command) { 
    var details = MovieDetails.builder()
        .title(Title.of(command.title()))
        .releaseDate(command.releaseDate() != null ? ReleaseDate.of(command.releaseDate()) : null)
        .score(command.score() != null ? Score.of(command.score()) : null)
        .originalLanguage(command.originalLanguage() != null ? LanguageCode.of(command.originalLanguage()) : null)
        .overview(command.overview() != null ? Overview.of(command.overview()) : null)
        .build();
    return SaveMovieCommand.of(TmdbId.of(command.tmdbId()), details, List.of());
  }
}
