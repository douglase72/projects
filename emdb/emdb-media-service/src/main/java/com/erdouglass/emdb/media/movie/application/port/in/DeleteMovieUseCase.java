package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.kernel.PublicId;

public interface DeleteMovieUseCase {

  void deleteById(PublicId id);
}
