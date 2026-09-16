package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.kernel.SaveResult;

/// Open-Host Service protects downstream consumers from changes in the Media 
/// service domain allowing them to evolve independently.   
public interface SaveMovieUseCase {

  SaveResult save(SaveMovieCommand command);
}
