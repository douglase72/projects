package com.erdouglass.emdb.media.application.port.inbound;

import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

/// Inbound (driving) port: removes a movie from the catalog.
///
/// Takes the [MoviePublicId] — the identity adapters are allowed to know — never
/// the internal [MovieId], which does not exist outside the hexagon.
public interface DeleteMovieUseCase {

  void delete(MoviePublicId id);
}
