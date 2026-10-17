package com.erdouglass.emdb.scraper.application.port.out;

import com.erdouglass.emdb.scraper.domain.event.MovieEvent;

public interface MovieEventProducer {

  void send(MovieEvent event);
}
