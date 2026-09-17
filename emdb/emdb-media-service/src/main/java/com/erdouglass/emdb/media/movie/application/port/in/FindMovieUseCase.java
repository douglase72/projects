package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Optional;

import com.erdouglass.emdb.media.kernel.PublicId;

public interface FindMovieUseCase {

  Optional<MovieView> findById(PublicId id);
}
