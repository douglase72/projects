package com.erdouglass.emdb.media.application.port.inbound;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

public interface UpdateMovieUseCase {

  UpdateResult update(@NotNull MoviePublicId id, @NotNull UpdateMovieCommand command);
}
