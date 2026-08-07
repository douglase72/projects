package com.erdouglass.emdb.media.application.port.inbound.movie;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.SaveResult;

public interface UpdateMovieUseCase {

  SaveResult update(@NotNull UpdateMovieCommand command);
}
