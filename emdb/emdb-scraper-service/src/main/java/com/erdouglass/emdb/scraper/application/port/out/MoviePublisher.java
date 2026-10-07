package com.erdouglass.emdb.scraper.application.port.out;

import com.erdouglass.emdb.scraper.messaging.MovieExtracted;

public interface MoviePublisher {

  void publish(MovieExtracted event);
}
