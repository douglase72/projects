package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.SaveResult;

/// Inbound port for editing a title the caller has already read.
///
/// Never creates. A catalogue id that matches nothing is an error rather than an
/// invitation to insert, because the caller is asserting that it read the title
/// — if it is gone, something has happened the caller needs to know about.
public interface UpdateMovieUseCase {

  /// Replaces the title's details, after checking that the caller's version is
  /// current.
  ///
  /// Differences are appended to the audit trail. When the incoming details
  /// already match, nothing is written and the result reports `UNCHANGED`, even
  /// though the version check still had to pass.
  ///
  /// @param command the intended state, the target id and the version the caller
  ///        read
  /// @return the catalogue id, the version afterwards, and which outcome occurred
  /// @throws MovieNotFoundException if no title carries the command's id
  /// @throws StalePersonException if the stored version differs from the one
  ///         supplied
  /// @throws LockedPersonException if the title is locked, including when the
  ///         incoming details are identical
  /// @throws IllegalArgumentException if the command's id is not a well-formed
  ///         catalogue id
  SaveResult update(UpdateMovieCommand command);
}
