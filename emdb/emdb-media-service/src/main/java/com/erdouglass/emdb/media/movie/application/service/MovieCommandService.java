package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieOutbox;
import com.erdouglass.emdb.media.movie.domain.model.Movie;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  
  @Inject
  MovieOutbox outbox;

  @Override
  @Transactional
  public void save(SaveMovieCommand command) {
    if (outbox.hasProcessed(command.id())) {
      return;
    }
    var movie = Movie.create(command.correlationId(), command.tmdbId(), command.details());
    movie.pullEvents().forEach(outbox::markProcessed);
  }
}
