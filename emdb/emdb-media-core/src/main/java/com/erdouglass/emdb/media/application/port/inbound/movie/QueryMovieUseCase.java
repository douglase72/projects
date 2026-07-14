package com.erdouglass.emdb.media.application.port.inbound.movie;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.media.application.port.inbound.movie.MovieView.MovieCredits;

public interface QueryMovieUseCase {

  MovieCredits findCreditsByMovieId(@NotNull @Positive Long id);
  
  MovieView findById(@NotNull @Positive Long id);
}
