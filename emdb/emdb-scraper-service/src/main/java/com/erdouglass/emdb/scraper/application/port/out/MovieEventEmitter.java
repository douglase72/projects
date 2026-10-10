package com.erdouglass.emdb.scraper.application.port.out;

import com.erdouglass.emdb.scraper.domain.event.MovieEvent;

public interface MovieEventEmitter {

  void emit(MovieEvent event);
}
