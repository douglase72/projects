package com.erdouglass.emdb.media.adapter.outbound.movie;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.application.port.outbound.MovieRepository;
import com.erdouglass.emdb.media.domain.movie.Movie;

@ApplicationScoped
class MoviePersistenceAdapter implements MovieRepository {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieCrudRepository repository;
  
  @Override
  public Movie insert(Movie movie) {
    return mapper.toMovie(repository.insert(mapper.toMovieEntity(movie)));        
  }
  
  @Override
  public Movie update(Movie movie) {
    return mapper.toMovie(repository.update(mapper.toMovieEntity(movie)));
  }  
  
  @Override
  public Optional<Movie> findBySourceId(String source, String sourceId) {
    return repository.findBySourceId(source, sourceId)
        .map(mapper::toMovie);
  }
}
