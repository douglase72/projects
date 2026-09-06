package com.erdouglass.emdb.media.movie.application.port.in;

public interface SaveMovieUseCase {

  MovieResult save(SaveMovie command);
}
