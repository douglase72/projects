package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.SaveResult;

/// Inbound port for freezing and releasing a title's details.
///
/// Separate from the update port because it changes no content: it changes
/// whether content may change. Kept separate so that the permission to curate is
/// separable from the permission to edit.
public interface LockMovieUseCase {

  /// Applies the lock state, after checking that the caller's version is current.
  ///
  /// Succeeds on a title that is already in the requested state, and succeeds on
  /// a locked title — otherwise a lock could never be lifted. Either way the
  /// write bumps the version, so the returned value is the one to hold going
  /// forward.
  ///
  /// @param command the target title, the version the caller read, and the
  ///        desired state
  /// @return the catalogue id and new version, always reported as an update
  /// @throws MovieNotFoundException if no title carries the command's id
  /// @throws StalePersonException if the stored version differs from the one
  ///         supplied
  SaveResult lock(LockMovieCommand command);
}
