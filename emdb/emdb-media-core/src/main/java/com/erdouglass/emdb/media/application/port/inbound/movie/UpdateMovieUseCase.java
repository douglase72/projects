package com.erdouglass.emdb.media.application.port.inbound.movie;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface UpdateMovieUseCase {
  
  MovieView update(@NotNull @Positive Long id, @NotNull @Valid UpdateMovie command);
}
