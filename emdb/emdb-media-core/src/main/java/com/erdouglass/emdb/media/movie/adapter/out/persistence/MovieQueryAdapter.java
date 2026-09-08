package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.movie.application.port.out.MovieQueryRepository;
import com.erdouglass.emdb.media.movie.application.port.out.MovieView;

@ApplicationScoped
class MovieQueryAdapter implements MovieQueryRepository {
  
  @Inject
  JakartaDataMovieQueryRepository repository;

  @Override
  public Optional<MovieView> findById(PublicId id) {
    return repository.findById(id.value());
  }
}
