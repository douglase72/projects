package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.SaveMovieCommand;

public interface SaveMovieUseCase {

  void save(SaveMovieCommand command);
}
