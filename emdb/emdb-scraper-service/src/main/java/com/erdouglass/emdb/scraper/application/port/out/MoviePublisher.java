package com.erdouglass.emdb.scraper.application.port.out;

import java.util.UUID;

import com.erdouglass.emdb.scraper.domain.model.Movie;

public interface MoviePublisher {

  void publish(UUID ingestId, Movie movie);
}
