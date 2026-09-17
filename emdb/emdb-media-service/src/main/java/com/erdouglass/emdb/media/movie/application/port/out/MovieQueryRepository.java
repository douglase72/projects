package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.movie.application.port.in.MovieView;

public interface MovieQueryRepository {

  Optional<MovieView> findById(PublicId id);
}
