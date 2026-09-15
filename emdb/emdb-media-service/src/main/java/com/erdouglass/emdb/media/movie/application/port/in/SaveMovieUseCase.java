package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.kernel.SaveResult;

public interface SaveMovieUseCase {

  SaveResult save(SaveMovieCommand command);
}
