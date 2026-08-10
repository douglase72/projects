package com.erdouglass.emdb.media.application.port.inbound.movie;

import jakarta.validation.constraints.NotNull;

public interface UpdateMovieUseCase {

  SaveResult update(@NotNull UpdateMovieCommand command);
}
