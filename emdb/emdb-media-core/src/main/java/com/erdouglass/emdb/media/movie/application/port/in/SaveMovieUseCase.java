package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand;

public interface SaveMovieUseCase {

  MovieResult save(SaveMovieCommand command);
}
