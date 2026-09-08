package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.dto.UpdateResult;

public interface UpdateMovieUseCase {

  UpdateResult update(UpdateMovieCommand command);
}
