package com.erdouglass.emdb.media.movie;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface MovieCommandService {

  MovieDto save(@NotNull @Valid SaveMovie command);
    
  MovieDto update(@NotNull @Valid UpdateMovie command);
  
  void delete(@NotNull @Positive Long id);
}
