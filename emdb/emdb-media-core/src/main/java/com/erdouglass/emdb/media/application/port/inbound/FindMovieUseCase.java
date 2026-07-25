package com.erdouglass.emdb.media.application.port.inbound;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

public interface FindMovieUseCase {

  Optional<Movie> findById(@NotNull MoviePublicId id);
}
