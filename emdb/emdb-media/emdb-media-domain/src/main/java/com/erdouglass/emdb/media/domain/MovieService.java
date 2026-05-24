package com.erdouglass.emdb.media.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.api.query.MovieResponse;

public interface MovieService {
  
  MovieResponse save(@NotNull @Valid SaveMovie command);
}
