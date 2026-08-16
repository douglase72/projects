package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.movie.domain.MoviePublicId;

/// Inbound port for removing a title from the catalogue.
///
/// Deliberately versionless: a delete has no state to merge, so refusing one as
/// stale would only force a re-read before repeating the same request. A locked
/// title is likewise no obstacle — the lock guards details, not existence.
public interface DeleteMovieUseCase {

  /// Removes the title, recording its final state in the audit trail first.
  ///
  /// The trail is closed out before the row goes, so the title's history ends
  /// with every populated field marked as removed rather than simply stopping.
  ///
  /// Not idempotent: deleting an id twice fails the second time, so that a
  /// client working from a stale list learns its view is out of date.
  ///
  /// @param id the catalogue id of the title to remove
  /// @throws MovieNotFoundException if no title carries `id`
  void delete(MoviePublicId id);
}
