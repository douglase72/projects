package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.media.movie.application.port.in.MovieView;
import com.erdouglass.emdb.media.shared.domain.model.MediaId;

public interface MovieQueryRepository {
  
  Optional<MovieView> findById(MediaId id);
}
