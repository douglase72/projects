package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.dto.SaveResult.Status;
import com.erdouglass.emdb.media.dto.UpdateResult;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.domain.event.MovieEvent;
import com.erdouglass.emdb.media.movie.domain.exception.MovieNotFoundException;
import com.erdouglass.emdb.media.movie.domain.model.Movie;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase, UpdateMovieUseCase {
  
  @Inject
  Event<MovieEvent> emitter;
  
  @Inject
  MovieCommandRepository movies;

  /// Save the movie described by the command to the database.
  /// 
  /// This method is idempotent with respect to the movies TMDB id. If a movie
  /// with a matching TMDB id does not already exist, one will be created. 
  /// Otherwise, the movie details are updated making retries safe.
  @Override
  @Transactional
  public SaveResult save(SaveMovieCommand command) {
    return movies.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  @Override
  @Transactional
  public UpdateResult update(UpdateMovieCommand command) {
    var existing = movies.findById(command.id())
        .orElseThrow(() -> new MovieNotFoundException(command.id().value().toString()));
    existing.checkVersion(command.version());
    existing.update(command.details());
    var updated = movies.update(existing);
    existing.pullEvents().forEach(emitter::fire);
    return UpdateResult.of(updated.id(), updated.version(), UpdateResult.Status.UPDATED);
  }
  
  private SaveResult insert(SaveMovieCommand command) {
    var movie = Movie.create(command.tmdbId(), command.details());
    var inserted = movies.insert(movie);
    movie.pullEvents().forEach(emitter::fire);
    return SaveResult.of(inserted.id(), Status.CREATED);
  }
  
  private SaveResult update(Movie existing, SaveMovieCommand command) {
    existing.update(command.details());
    var updated = movies.update(existing);
    existing.pullEvents().forEach(emitter::fire);
    return SaveResult.of(updated.id(), Status.UPDATED);
  }
}
