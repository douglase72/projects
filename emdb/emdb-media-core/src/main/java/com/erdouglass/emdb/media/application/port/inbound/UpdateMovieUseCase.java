package com.erdouglass.emdb.media.application.port.inbound;

import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

public interface UpdateMovieUseCase {

  UpdateResult update(MoviePublicId id, UpdateMovieCommand command);
}
