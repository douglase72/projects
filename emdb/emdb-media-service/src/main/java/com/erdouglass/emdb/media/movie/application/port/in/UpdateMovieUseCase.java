package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.kernel.UpdateResult;

/// Open-Host Service protects downstream consumers from changes in the Media 
/// service domain allowing them to evolve independently.
public interface UpdateMovieUseCase {

  UpdateResult update(UpdateMovieCommand command);
}
