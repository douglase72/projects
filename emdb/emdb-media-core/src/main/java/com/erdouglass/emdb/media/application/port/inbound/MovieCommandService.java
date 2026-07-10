package com.erdouglass.emdb.media.application.port.inbound;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface MovieCommandService {
  
  MovieView update(@NotNull @Valid UpdateMovie command);
  
  void deleteById(@NotNull @Positive Long id);
}
