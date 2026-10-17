package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.application.port.out.MovieOutbox;
import com.erdouglass.emdb.media.movie.domain.event.MovieEvent;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.shared.application.SaveResult;
import com.erdouglass.emdb.media.shared.application.SaveResult.Status;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {  
  
  @Inject
  Event<MovieEvent> emitter;
  
  @Inject
  MovieCommandRepository movies;
  
  @Inject
  MovieOutbox outbox;

  @Override
  @Transactional
  public SaveResult save(SaveMovieCommand command) {
    return movies.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  private SaveResult insert(SaveMovieCommand command) {
    var movie = Movie.create(command.correlationId(), command.tmdbId(), command.details());
    movies.insert(movie);
    movie.pullEvents().forEach(e -> {
      outbox.save(e);
      emitter.fire(e);
    });
    return SaveResult.of(movie.id(), Status.CREATED);
  }
  
  private SaveResult update(Movie existing, SaveMovieCommand command) {
    existing.update(command.correlationId(), command.details());
    movies.update(existing);
    existing.pullEvents().forEach(e -> {
      outbox.save(e);
      emitter.fire(e);
    });
    return SaveResult.of(existing.id(), Status.UPDATED);
  }
}
