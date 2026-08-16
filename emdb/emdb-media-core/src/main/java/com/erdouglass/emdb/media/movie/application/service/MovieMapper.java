package com.erdouglass.emdb.media.movie.application.service;

import com.erdouglass.emdb.media.UpsertMovieCommand;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.movie.domain.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.ReleaseDate;

final class MovieMapper {

  MovieMapper() {}
  
  public static MovieDetails toMovieDetails(UpsertMovieCommand command) {
    return MovieDetails.builder()
        .title(Title.of(command.title()))
        .releaseDate(command.releaseDate().map(ReleaseDate::from).orElse(null))
        .score(command.score().map(Score::of).orElse(null))
        .originalLanguage(command.originalLanguage().map(LanguageCode::of).orElse(null))
        .overview(command.overview().map(Overview::of).orElse(null))
        .build();
  }
}
