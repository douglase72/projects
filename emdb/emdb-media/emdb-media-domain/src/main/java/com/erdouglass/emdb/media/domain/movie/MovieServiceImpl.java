package com.erdouglass.emdb.media.domain.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.api.query.MovieResponse;
import com.erdouglass.emdb.media.domain.MovieService;

@ApplicationScoped
class MovieServiceImpl implements MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieServiceImpl.class);
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;

  @Override
  @Transactional
  public MovieResponse save(final SaveMovie command) {
    Movie savedMovie;
    var existingMovie = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existingMovie == null) {
      savedMovie = repository.insert(mapper.toMovie(command));
    } else {
      mapper.merge(command, existingMovie);
      savedMovie = repository.update(existingMovie);
    }
    LOGGER.infof("Saved: %s", savedMovie);
    return mapper.toMovieResponse(savedMovie);
  }
}
