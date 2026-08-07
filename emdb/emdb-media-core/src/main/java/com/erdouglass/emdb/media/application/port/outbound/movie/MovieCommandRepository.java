package com.erdouglass.emdb.media.application.port.outbound.movie;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

public interface MovieCommandRepository {

  Movie insert(@NotNull Movie movie);
  
  Movie update(@NotNull Movie movie);
  
  void deleteByPublicId(MoviePublicId publicId);
  
  Optional<Movie> findByPublicId(@NotNull MoviePublicId publicId);
  
  Optional<Movie> findByTmdbId(@NotNull TmdbId tmdbId);
}
