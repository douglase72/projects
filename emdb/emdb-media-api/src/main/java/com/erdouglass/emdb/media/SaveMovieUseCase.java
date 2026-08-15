package com.erdouglass.emdb.media;

import jakarta.validation.constraints.NotNull;

public interface SaveMovieUseCase {

  SaveResult save(@NotNull SaveMovieCommand command);
}
