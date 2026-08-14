package com.erdouglass.emdb.media.application.port.inbound.movie;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.adapter.inbound.movie.MovieView;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

public interface FindMovieUseCase {

  Optional<MovieView> findById(@NotNull MoviePublicId id);
}
