package com.erdouglass.emdb.media.core.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.command.MovieCommandService;
import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.query.MovieDto;
import com.erdouglass.emdb.media.query.MovieQueryService;

@ApplicationScoped
class MovieService implements MovieCommandService, MovieQueryService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository movieRepository;
  
  @Override
  @Transactional
  public MovieDto save(SaveMovie command) {
    var movie = movieRepository.insert(mapper.toMovie(command));
    LOGGER.infof("Saved: %s", movie);
    return mapper.toMovieDto(movie);
  }
  
  @Override
  @Transactional
  public MovieDto findById(Long id) {
    throw new UnsupportedOperationException();
  }
}
