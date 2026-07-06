package com.erdouglass.emdb.media.application.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.media.application.port.in.MovieCommandService;
import com.erdouglass.emdb.media.application.port.in.MovieQueryService;
import com.erdouglass.emdb.media.application.port.in.MovieView;
import com.erdouglass.emdb.media.application.port.in.UpdateMovie;

class MovieService implements MovieCommandService, MovieQueryService {

  @Override
  public MovieView findById(@NotNull @Positive Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MovieView update(@NotNull @Valid UpdateMovie command) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteById(@NotNull @Positive Long id) {
    throw new UnsupportedOperationException();
  }
}
