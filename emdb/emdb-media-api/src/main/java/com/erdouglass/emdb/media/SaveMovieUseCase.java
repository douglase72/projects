package com.erdouglass.emdb.media;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface SaveMovieUseCase {

  SaveResult save(@NotNull @Valid SaveMovie command);
}
