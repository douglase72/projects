package com.erdouglass.emdb.media.application.port.inbound;

import com.erdouglass.emdb.media.domain.movie.UpdateMovie;

/// Inbound (driving) port: the use-case contract for a person editing a
/// movie they previously fetched.
///
/// [SaveMovieUseCase]'s counterpart, carrying the asymmetries the two paths
/// earned: addressed by public id rather than source identity, can never
/// create, and demands proof of version — the command's claimed snapshot is
/// what the optimistic check enforces.
public interface UpdateMovieUseCase {

  /// Applies the edit to the movie addressed by `id` (the public id string,
  /// `"mv_42"`).
  ///
  /// @return the address echoed for correlation and the *newly minted*
  ///         version — clients echo it on their next edit, never compute it
  /// @throws MovieNotFoundException if `id` is malformed, unknown, or not a movie
  /// @throws StaleVersionException  if the claimed version lost
  UpdateResult update(String id, UpdateMovie command);
}
