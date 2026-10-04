package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.common.messaging.Correlation;
import com.erdouglass.emdb.media.shared.application.SaveResult;

public interface SaveMovieUseCase {
  
  default SaveResult save(SaveMovieCommand command) {
    return save(command, Correlation.empty());
  }

  SaveResult save(SaveMovieCommand command, Correlation correlation);
}
