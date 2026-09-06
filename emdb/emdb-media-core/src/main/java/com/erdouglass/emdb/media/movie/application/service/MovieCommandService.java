package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.movie.application.port.in.MovieResult;
import com.erdouglass.emdb.media.movie.application.port.in.MovieResult.Status;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovie;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.Movie;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  
  @Inject
  MovieCommandRepository movies;

  @Override
  public MovieResult save(SaveMovieCommand command) {
    return movies.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  private MovieResult insert(SaveMovieCommand command) {
    var movie = Movie.create(command);
    var inserted = movies.insert(movie);
    return MovieResult.of(inserted.id(), inserted.version(), Status.CREATED);
  }
  
  private MovieResult update(Movie existing, SaveMovieCommand command) {
    // TODO: Mapstruct can handle this better than hand jamming it.
    var cmd = UpdateMovie.builder()
        .publicId(existing.publicId().orElseThrow())
        .version(existing.version())
        .title(command.title())
        .releaseDate(command.releaseDate())
        .score(command.score())
        .originalLanguage(command.originalLanguage())
        .overview(command.overview())
        .build();
    existing.update(cmd);
    var updated = movies.update(existing);
    return MovieResult.of(updated.id(), updated.version(), Status.UPDATED);
  }
}
