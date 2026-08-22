package com.erdouglass.emdb.media.movie.application.service;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.movie.MovieCommand;
import com.erdouglass.emdb.media.movie.domain.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.ReleaseDate;

final class MovieMapper {

  private MovieMapper() {}
  
  public static MovieDetails toMovieDetails(MovieCommand command) {
    return new MovieDetails(
        Title.of(command.title()),
        command.releaseDate().map(ReleaseDate::from),
        command.score().map(Score::of),
        command.originalLanguage().map(LanguageCode::of),
        command.overview().map(Overview::of));
  }
}
