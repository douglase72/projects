package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Optional;

import com.erdouglass.emdb.media.shared.domain.model.MediaId;

public interface FindMovieUseCase {

  Optional<MovieView> findById(MediaId id);
}
