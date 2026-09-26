package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.shared.application.SaveResult;
import com.erdouglass.emdb.media.shared.application.SaveResult.Status;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieCommandService.class);
  
  @Override
  public SaveResult save(SaveMovieCommand command) {
    var movie = Movie.create(command.tmdbId(), command.details());
    LOGGER.info(movie);
    return SaveResult.of(movie.id(), Status.CREATED);
  }
}
