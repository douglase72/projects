package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.movie.application.port.in.MovieCreditView;
import com.erdouglass.emdb.media.movie.application.port.in.MovieView;
import com.erdouglass.emdb.media.movie.application.port.out.MovieQueryRepository;
import com.erdouglass.emdb.media.movie.domain.model.MoviePublicId;

@ApplicationScoped
class MovieQueryAdapter implements MovieQueryRepository {
  
  @Inject
  JakartaDataMovieQueryRepository repository;

  @Override
  public Optional<MovieView> findById(MoviePublicId id) {
    return repository.findById(id.toLong());
  }

  @Override
  public List<MovieCreditView> findCreditsByMovieId(MoviePublicId id) {
    return repository.findCreditsByMovieId(id.toLong());
  }
}
