package com.erdouglass.emdb.media.adapter.outbound.persistence.movie;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.adapter.inbound.graphql.movie.MovieView;
import com.erdouglass.emdb.media.application.port.outbound.movie.MovieQueryRepository;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

@ApplicationScoped
class MovieQueryAdapter implements MovieQueryRepository {
  
  @Inject
  JakartaDataMovieQueryRepository repository;

  @Override
  public Optional<MovieView> findById(MoviePublicId id) {
    return repository.findById(id.toLong());
  }
}
