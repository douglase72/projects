package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.List;

import com.erdouglass.emdb.media.kernel.PublicId;

public interface FindMovieCreditsUseCase {

  List<MovieCreditView> findByMovieId(PublicId id);
}
