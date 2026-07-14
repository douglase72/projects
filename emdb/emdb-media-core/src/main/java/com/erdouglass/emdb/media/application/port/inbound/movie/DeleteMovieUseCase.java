package com.erdouglass.emdb.media.application.port.inbound.movie;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface DeleteMovieUseCase {

  void deleteById(@NotNull @Positive Long id);
}
