package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.UpdateMovieCommand;
import com.erdouglass.emdb.media.domain.movie.Movie;

@ApplicationScoped
class MovieMapper {

  public Movie merge(Movie movie, SaveMovieCommand command) {
    return Movie.builder()
        .id(movie.id())
        .publicId(movie.publicId().orElseThrow())
        .sourceId(movie.sourceId())
        .title(command.title())
        .releaseDate(command.releaseDate())
        .originalLanguage(command.originalLanguage())        
        .build();    
  }
  
  public Movie merge(Movie movie, UpdateMovieCommand command) {
    return Movie.builder()
        .id(movie.id())
        .publicId(movie.publicId().orElseThrow())
        .sourceId(movie.sourceId())
        .version(command.version())
        .title(command.title())
        .releaseDate(command.releaseDate())
        .originalLanguage(command.originalLanguage())      
        .build();
  }  
}
