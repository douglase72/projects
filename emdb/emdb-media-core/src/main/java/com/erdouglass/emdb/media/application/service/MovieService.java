package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.application.port.in.MovieCommandService;
import com.erdouglass.emdb.media.application.port.in.MovieQueryService;
import com.erdouglass.emdb.media.application.port.in.MovieView;
import com.erdouglass.emdb.media.application.port.in.UpdateMovie;
import com.erdouglass.emdb.media.domain.movie.MovieRepository;

@ApplicationScoped
class MovieService implements MovieCommandService, MovieQueryService {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;

  @Override
  public MovieView findById(Long id) {
    return repository.findById(id)
        .map(mapper::toMovieView)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));        
  }

  @Override
  public MovieView update(UpdateMovie command) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteById(Long id) {
    throw new UnsupportedOperationException();
  }
}
