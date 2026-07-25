package com.erdouglass.emdb.media;

import jakarta.validation.constraints.NotNull;

/// Inbound (driving) port: the use-case contract for creating or updating a
/// movie from an external source payload.
///
/// Ports on this side of the hexagon are the application's published verbs.
/// Adapters (REST today, importers or messaging tomorrow) call them; they
/// speak in plain command/result records so that no adapter dialect and no
/// domain type crosses the boundary in either direction.
public interface SaveMovieUseCase {

  SaveResult save(@NotNull SaveMovieCommand command);
}
