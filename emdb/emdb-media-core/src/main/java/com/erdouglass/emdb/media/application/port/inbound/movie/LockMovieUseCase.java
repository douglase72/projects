package com.erdouglass.emdb.media.application.port.inbound.movie;

import jakarta.validation.constraints.NotNull;

public interface LockMovieUseCase {

  SaveResult lock(@NotNull LockMovieCommand command);
}
