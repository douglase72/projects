package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.shared.domain.model.MediaId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public interface MovieCommandRepository {

  Movie insert(Movie movie);
  
  Movie update(Movie movie);
  
  void deleteById(MediaId id);
  
  Optional<Movie> findById(MediaId id);
    
  Optional<Movie> findByTmdbId(TmdbId tmdbId);
}
