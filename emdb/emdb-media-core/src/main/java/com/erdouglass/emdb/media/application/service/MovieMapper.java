package com.erdouglass.emdb.media.application.service;

import com.erdouglass.emdb.media.UpsertMovieCommand;
import com.erdouglass.emdb.media.domain.movie.MovieDetails;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.Title;
import com.erdouglass.emdb.media.domain.shared.LanguageCode;
import com.erdouglass.emdb.media.domain.shared.Overview;
import com.erdouglass.emdb.media.domain.shared.Score;

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
