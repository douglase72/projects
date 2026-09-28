package com.erdouglass.emdb.scraper.application.port.out;

import com.erdouglass.emdb.scraper.domain.model.Movie;

public interface MoviePublisher {

  void publish(Movie movie);
}
