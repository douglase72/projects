package com.erdouglass.emdb.media.adapter.outbound.movie;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.PublicId;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.Title;
import com.erdouglass.emdb.media.domain.shared.SourceId;
import com.erdouglass.emdb.media.domain.shared.SourceId.Source;

@ApplicationScoped
class MovieMapper {
  
  public MovieEntity toMovieEntity(Movie movie) {
    var entity = new MovieEntity();
    entity.setId(movie.id().value());
    entity.setSource(movie.sourceId().source().toString());
    entity.setSourceId(movie.sourceId().id());
    entity.setTitle(movie.title().value());
    entity.setReleaseDate(movie.releaseDate().value());
    return entity;
  }
  
  public Movie toMovie(MovieEntity entity) {
    return Movie.builder()
        .id(new MovieId(entity.getId()))
        .publicId(new PublicId(entity.getPublicId()))
        .sourceId(new SourceId(Source.from(entity.getSource()), entity.getSourceId()))
        .title(new Title(entity.getTitle()))
        .releaseDate(new ReleaseDate(entity.getReleaseDate()))
        .build();
  }
}
