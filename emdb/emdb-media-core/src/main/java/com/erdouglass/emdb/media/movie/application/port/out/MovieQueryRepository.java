package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.List;
import java.util.Optional;

import com.erdouglass.emdb.media.movie.application.port.in.MovieCreditView;
import com.erdouglass.emdb.media.movie.application.port.in.MovieView;
import com.erdouglass.emdb.media.movie.domain.model.MoviePublicId;

public interface MovieQueryRepository {

  Optional<MovieView> findById(MoviePublicId id);
  
  List<MovieCreditView> findCreditsByMovieId(MoviePublicId id);
}
