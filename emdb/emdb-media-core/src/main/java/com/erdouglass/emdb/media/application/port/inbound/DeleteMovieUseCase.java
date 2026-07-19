package com.erdouglass.emdb.media.application.port.inbound;

import com.erdouglass.emdb.media.domain.shared.PublicId;

/// Inbound (driving) port: removes a movie from the catalog.
///
/// Takes the [PublicId] — the identity adapters are allowed to know — never
/// the internal [MovieId], which does not exist outside the hexagon.
public interface DeleteMovieUseCase {

  void delete(PublicId id);
}
