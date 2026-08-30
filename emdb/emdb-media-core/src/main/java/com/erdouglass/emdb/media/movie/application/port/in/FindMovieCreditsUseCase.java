package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.List;

import com.erdouglass.emdb.media.movie.domain.model.MoviePublicId;

public interface FindMovieCreditsUseCase {

  List<MovieCreditView> findByMovieId(MoviePublicId movieId);
}
