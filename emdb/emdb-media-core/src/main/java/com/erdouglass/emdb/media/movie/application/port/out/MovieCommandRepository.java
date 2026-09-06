package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.SurrogateId;
import com.erdouglass.emdb.media.movie.domain.model.Movie;

public interface MovieCommandRepository {

  Movie insert(Movie movie);
  
  Movie update(Movie movie);
  
  Optional<Movie> findBySurrogateId(SurrogateId surrogateId);
  
  Optional<Movie> findByTmdbId(TmdbId tmdbId);
}
