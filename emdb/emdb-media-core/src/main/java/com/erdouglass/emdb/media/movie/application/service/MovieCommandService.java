package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.movie.application.port.in.MovieResult;
import com.erdouglass.emdb.media.movie.application.port.in.MovieResult.Status;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovie;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.domain.model.Movie;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  
  @Inject
  MovieCommandRepository movies;
  
  @Inject
  CommandMapper mapper;

  @Override
  public MovieResult save(SaveMovie command) {
    return movies.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  private MovieResult insert(SaveMovie command) {
    var movie = Movie.create(command);
    var inserted = movies.insert(movie);
    return MovieResult.of(inserted.id(), inserted.version(), Status.CREATED);
  }
  
  private MovieResult update(Movie existing, SaveMovie command) {
    var cmd = mapper.toUpdateMovie(existing.id(), existing.version(), command);
    existing.update(cmd);
    var updated = movies.update(existing);
    return MovieResult.of(updated.id(), updated.version(), Status.UPDATED);
  }
}
