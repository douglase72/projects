package com.erdouglass.emdb.media.movie;

import com.erdouglass.emdb.media.Result;
import com.erdouglass.emdb.media.TmdbId;

/// Ingestion port: upserts a title keyed by its TMDB id.
///
/// This is the entry point used when a caller pushes the state of a title as an
/// upstream source knows it, without knowing or caring whether the catalogue
/// already holds that title. Because the key is [TmdbId] rather than the
/// catalogue id, the caller needs no prior read.
///
/// Contrast with `UpdateMovieUseCase`, which keys on the catalogue id and
/// requires the caller to supply the version it read. This port performs no
/// optimistic-lock check: concurrent saves are last-writer-wins.
public interface SaveMovieUseCase {

  /// Creates the title if no movie carries this TMDB id, otherwise replaces the
  /// stored details with the command's details.
  ///
  /// Every field-level difference is appended to the audit trail. When the
  /// incoming details already match what is stored, nothing is written and the
  /// result reports [Result.Status#UNCHANGED].
  ///
  /// @param command the complete intended state of the title
  /// @return the catalogue id, the version as of the end of the call, and which
  ///         of create / update / no-op occurred
  /// @throws LockedMovieException if a movie with this TMDB id exists and is
  ///         locked, even when the incoming details are identical
  Result save(SaveMovieCommand command);
}
