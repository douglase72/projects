package com.erdouglass.emdb.media.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.movie.MovieResponse;
import com.erdouglass.emdb.media.movie.SaveMovie;

public interface MovieService {
  
  MovieResponse save(@NotNull @Valid SaveMovie command);
}
