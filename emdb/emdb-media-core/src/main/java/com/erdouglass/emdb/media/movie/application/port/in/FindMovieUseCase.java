package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Optional;

import com.erdouglass.emdb.media.movie.application.port.out.MovieView;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;

/// Inbound port for reading a single title.
///
/// The read half of the CQRS split: implementations project from the database
/// rather than loading the aggregate, so nothing here can be used to mutate and
/// no domain invariant is re-applied on the way out.
public interface FindMovieUseCase {

  /// Looks up a title by its catalogue id.
  ///
  /// Absence is a normal result, not a failure — the caller decides whether it
  /// means a `404`, a null GraphQL field, or something else.
  ///
  /// @param id the catalogue id
  /// @return the projected title, or empty if none carries that id
  Optional<MovieView> findById(MoviePublicId id);
}
