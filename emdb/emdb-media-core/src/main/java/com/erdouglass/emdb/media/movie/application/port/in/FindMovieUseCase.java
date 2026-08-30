package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Optional;

import com.erdouglass.emdb.media.movie.domain.model.MoviePublicId;

public interface FindMovieUseCase {
  
  Optional<MovieView> findById(MoviePublicId id);
}
