package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.media.movie.adapter.in.graphql.MovieView;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;

/// Outbound port for client-facing reads.
///
/// Returns the view rather than the aggregate, which is the point: a read served
/// through this port cannot be used to mutate, and implementations are free to
/// project, denormalise or cache without the domain noticing.
public interface MovieQueryRepository {

  /// Reads a single title by catalogue id.
  ///
  /// @param id the catalogue id
  /// @return the projected title, or empty if none carries that id
  Optional<MovieView> findById(MoviePublicId id);
}
