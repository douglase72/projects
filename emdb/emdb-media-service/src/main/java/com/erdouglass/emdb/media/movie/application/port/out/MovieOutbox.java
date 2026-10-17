package com.erdouglass.emdb.media.movie.application.port.out;

import com.erdouglass.emdb.media.movie.domain.event.MovieEvent;

public interface MovieOutbox {

  void save(MovieEvent event);
}
