package com.erdouglass.emdb.media.application.port.inbound;

import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

/// Inbound (driving) port: removes a movie from the catalog.
///
/// Contract: absence is a failure here, not a no-op. Deleting an unknown id
/// throws MovieNotFoundException — the command-side half of the module-wide
/// rule: queries report absence (Optional), commands demand existence. The
/// REST adapter translates the exception to a 404.
public interface DeleteMovieUseCase {

  void delete(MoviePublicId id);
}
