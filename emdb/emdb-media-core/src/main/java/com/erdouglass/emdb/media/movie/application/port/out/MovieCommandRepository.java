package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.movie.domain.model.Movie;

public interface MovieCommandRepository {

  Movie insert(Movie movie);
  
  Movie update(Movie movie);
  
  Optional<Movie> findByTmdbId(TmdbId tmdbId);
}
