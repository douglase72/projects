package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.UpdateMovieCommand;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.shared.OriginalLanguage;
import com.erdouglass.emdb.media.domain.shared.Title;
import com.erdouglass.emdb.media.domain.shared.Version;

@ApplicationScoped
class MovieMapper {

  public Movie merge(Movie movie, SaveMovieCommand command) {
    return Movie.builder()
        .id(movie.id())
        .publicId(movie.publicId().orElseThrow())
        .sourceId(movie.sourceId())
        .title(Title.of(command.title()))
        .releaseDate(ReleaseDate.of(command.releaseDate()))
        .originalLanguage(OriginalLanguage.of(command.originalLanguage()))        
        .build();    
  }
  
  public Movie merge(Movie movie, UpdateMovieCommand command) {
    return Movie.builder()
        .id(movie.id())
        .publicId(movie.publicId().orElseThrow())
        .sourceId(movie.sourceId())
        .version(Version.of(command.version()))
        .title(Title.of(command.title()))
        .releaseDate(ReleaseDate.of(command.releaseDate()))
        .originalLanguage(OriginalLanguage.of(command.originalLanguage()))        
        .build();
  }  
}
