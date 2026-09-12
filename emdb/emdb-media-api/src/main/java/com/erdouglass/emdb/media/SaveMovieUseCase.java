package com.erdouglass.emdb.media;

/// Open-Host Service protects the Ingest service from changes in the Media 
/// service domain. This allows the Ingest service and the Media service to
/// evolve independently.   
public interface SaveMovieUseCase {

  SaveResult save(SaveMovieCommand command);
}
