package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.movie.application.port.in.MovieCreditView;
import com.erdouglass.emdb.media.movie.application.port.in.MovieView;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCreditQueryRepository;
import com.erdouglass.emdb.media.movie.application.port.out.MovieQueryRepository;

@ApplicationScoped
class MovieQueryAdapter implements MovieQueryRepository, MovieCreditQueryRepository {
  
  @Inject
  JakartaDataMovieCreditQueryRepository credits;
  
  @Inject
  JakartaDataMovieQueryRepository repository;

  @Override
  public Optional<MovieView> findById(PublicId id) {
    return repository.findById(id.value());
  }

  @Override
  public List<MovieCreditView> findByMovieId(PublicId id) {
    return credits.findByMovieId(id.value());
  }
}
