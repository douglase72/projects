package com.erdouglass.emdb.media.movie.application.port.in;

public interface SaveMovieUseCase {

  Result save(SaveMovieCommand command);
}
