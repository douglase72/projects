package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.kernel.Result;

public interface SaveMovieUseCase {

  Result save(SaveMovieCommand command);
}
