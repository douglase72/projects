package com.erdouglass.emdb.media.application.port.inbound;

public interface UpdateMovieUseCase {

  UpdateResult update(String id, UpdateMovieCommand command);
}
