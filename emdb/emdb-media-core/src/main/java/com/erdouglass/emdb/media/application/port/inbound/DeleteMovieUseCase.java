package com.erdouglass.emdb.media.application.port.inbound;

import com.erdouglass.emdb.media.domain.movie.PublicId;

public interface DeleteMovieUseCase {

  void delete(PublicId id);
}
