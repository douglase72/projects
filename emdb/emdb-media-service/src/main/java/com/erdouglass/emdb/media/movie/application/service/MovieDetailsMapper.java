package com.erdouglass.emdb.media.movie.application.service;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

final class MovieDetailsMapper {

  private MovieDetailsMapper() { }
  
  public static MovieDetails toMovieDetails(SaveMovieCommand command) {
    return MovieDetails.builder()
        .title(Title.of(command.title()))
        .releaseDate(command.releaseDate() != null ? ReleaseDate.from(command.releaseDate()) : null)
         .score(command.score() != null ? Score.of(command.score()) : null)
        .originalLanguage(command.originalLanguage() != null ? LanguageCode.of(command.originalLanguage()) : null)
        .overview(command.overview() != null ? Overview.of(command.overview()) : null)
        .build();
  }
  
  public static MovieDetails toMovieDetails(UpdateMovieCommand command) {
    return MovieDetails.builder()
        .title(Title.of(command.title()))
        .releaseDate(command.releaseDate() != null ? ReleaseDate.from(command.releaseDate()) : null)
         .score(command.score() != null ? Score.of(command.score()) : null)
        .originalLanguage(command.originalLanguage() != null ? LanguageCode.of(command.originalLanguage()) : null)
        .overview(command.overview() != null ? Overview.of(command.overview()) : null)
        .build();
  }
}
