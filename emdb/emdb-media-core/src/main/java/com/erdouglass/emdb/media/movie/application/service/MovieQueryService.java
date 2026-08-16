package com.erdouglass.emdb.media.movie.application.service;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.movie.adapter.in.graphql.MovieView;
import com.erdouglass.emdb.media.movie.application.port.in.FindMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieQueryRepository;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;

/// Serves client reads of the movie catalogue.
///
/// Deliberately a pass-through. The read side has no rules to enforce — no
/// aggregate to load, no version to check, no lock to respect — so the class
/// exists to keep the port and the adapter from depending on one another, not to
/// add behaviour. Resist the pull to put filtering or shaping here; that belongs
/// in the projection.
///
/// Carries no transaction: reads run in whatever context the caller provides, or
/// in none.
@ApplicationScoped
class MovieQueryService implements FindMovieUseCase {
  
  @Inject
  MovieQueryRepository query;

  /// Looks up a title by its catalogue id.
  ///
  /// @param id the catalogue id
  /// @return the projected title, or empty if none carries that id
  @Override
  public Optional<MovieView> findById(MoviePublicId id) {
    return query.findById(id);
  }
}
