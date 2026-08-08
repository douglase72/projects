package com.erdouglass.emdb.media.movie;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.SaveResult;

public interface SaveMovieUseCase {

  SaveResult save(@NotNull SaveMovieCommand command);
}
