package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.movie.application.port.out.MovieQueryRepository;
import com.erdouglass.emdb.media.movie.application.port.out.MovieView;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;

/// Serves client reads by projecting straight from the database.
///
/// The counterpart to [MovieCommandAdapter], and deliberately thinner: there is
/// no aggregate to rebuild and no invariant to re-apply, so the class only
/// converts the catalogue id into a database key and hands back what the query
/// produced. A read here loads no entity and takes no lock.
@ApplicationScoped
class MovieQueryAdapter implements MovieQueryRepository {
  
  @Inject
  JakartaDataMovieQueryRepository repository;

  /// Projects a single title by its catalogue id.
  ///
  /// @param id the catalogue id, e.g. `mv_42`
  /// @return the projected title, or empty if none carries that id
  @Override
  public Optional<MovieView> findById(MoviePublicId id) {
    return repository.findById(id.toLong());
  }
}
