package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.List;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.movie.application.port.in.MovieCreditView;

public interface MovieCreditQueryRepository {

  List<MovieCreditView> findByMovieId(PublicId id);
}
