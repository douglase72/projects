package com.erdouglass.emdb.media.movie;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.media.movie.MovieDto.MovieCredits;

public interface MovieQueryService {
  
  MovieCredits findCreditsByMovieId(@NotNull @Positive Long id);
  
  MovieDto findById(@NotNull @Positive Long id);
}
