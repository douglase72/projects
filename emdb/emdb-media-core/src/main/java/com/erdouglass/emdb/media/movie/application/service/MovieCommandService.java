package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.kernel.Result;
import com.erdouglass.emdb.media.kernel.Result.Status;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.domain.model.Movie;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieCommandService.class);
  
  @Inject
  MovieCommandRepository movies;

  /// Save the movie described by the command.
  /// 
  /// Create the movie if it does not already exist, otherwise update the 
  /// existing movie.
  /// 
  /// @param command the command that describes the [Movie]
  /// @return the result
  @Override
  @Transactional
  public Result save(SaveMovieCommand command) {
    return movies.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  private Result insert(SaveMovieCommand command) {
    var movie = Movie.create(command.tmdbId(), command.details());
    var inserted = movies.insert(movie);
    LOGGER.infof("Saved: %s", inserted);
    return Result.of(inserted.id(), inserted.version(), Status.CREATED);
  }
  
  private Result update(Movie existing, SaveMovieCommand command) {
    existing.update(command.details());
    var updated = movies.update(existing);
    LOGGER.infof("Saved: %s", updated);
    return Result.of(updated.id(), updated.version(), Status.UPDATED);
  }
}
