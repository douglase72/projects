package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.shared.application.SaveResult;

public interface SaveMovieUseCase {

  SaveResult save(SaveMovieCommand command);
}
