package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieCommandService.class);
  
  @Override
  public void save(SaveMovieCommand command) {
    var details = MovieDetails.builder()
        .title(Title.of(command.title()))
        .releaseDate(command.releaseDate() != null ? 
            ReleaseDate.of(DateTimeFactory.from(command.releaseDate())) : null)
        .score(command.score() != null ? Score.of(command.score()) : null)
        .originalLanguage(command.originalLanguage() != null ? LanguageCode.of(command.originalLanguage()) : null)
        .overview(command.overview() != null ? Overview.of(command.overview()) : null)
        .build();
    var movie = Movie.create(TmdbId.of(command.tmdbId()), details);
    LOGGER.infof("movie: %s", movie);
  }
}
