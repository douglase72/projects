package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.movie.domain.event.MovieEvent;

public interface ReportMovieFailureUseCase {

  void report(MovieEvent event);
}
