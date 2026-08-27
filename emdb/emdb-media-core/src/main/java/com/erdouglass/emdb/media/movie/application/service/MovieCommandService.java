package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.application.port.out.Result;
import com.erdouglass.emdb.media.movie.application.port.out.Result.Status;
import com.erdouglass.emdb.media.movie.domain.Movie;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;
import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand;
import com.erdouglass.emdb.media.person.application.port.out.PersonDirectory;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  
  @Inject
  MovieCommandRepository movies;
  
  @Inject
  PersonDirectory people;

  @Override
  @Transactional
  public Result save(SaveMovieCommand command) {
    return movies.findBySourceId(command.sourceId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  private Result insert(SaveMovieCommand command) {
    Movie movie = Movie.create(command);
    var inserted = movies.add(movie);
    return Result.of(
        inserted.publicId().map(MoviePublicId::value).orElseThrow(), 
        inserted.version().value(), 
        Status.CREATED);
  }
  
  private Result update(Movie existing, SaveMovieCommand command) {
    existing.update(command);
    movies.update(existing);
    throw new UnsupportedOperationException();
  }
}
