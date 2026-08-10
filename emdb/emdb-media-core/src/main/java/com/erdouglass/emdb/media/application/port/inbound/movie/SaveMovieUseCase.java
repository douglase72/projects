package com.erdouglass.emdb.media.application.port.inbound.movie;

import jakarta.validation.constraints.NotNull;

public interface SaveMovieUseCase {

  SaveResult save(@NotNull SaveMovieCommand command);
}
