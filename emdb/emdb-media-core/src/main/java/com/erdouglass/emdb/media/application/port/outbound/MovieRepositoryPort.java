package com.erdouglass.emdb.media.application.port.outbound;

import com.erdouglass.emdb.media.domain.movie.Movie;

public interface MovieRepositoryPort {

  Movie save(Movie movie);
}
