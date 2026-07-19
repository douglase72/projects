package com.erdouglass.emdb.media.adapter.outbound.movie;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.media.MediaType;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.shared.OriginalLanguage;
import com.erdouglass.emdb.media.domain.shared.PublicId;
import com.erdouglass.emdb.media.domain.shared.SourceId;
import com.erdouglass.emdb.media.domain.shared.Title;
import com.erdouglass.emdb.media.domain.shared.Version;
import com.erdouglass.emdb.media.domain.shared.SourceId.Source;

@ApplicationScoped
class MovieMapper {
  
  public MovieEntity toMovieEntity(Movie movie) {
    var entity = new MovieEntity(movie.id().value());
    entity.setPublicId(movie.publicId().map(PublicId::value).orElse(null));
    entity.setSource(movie.sourceId().source().toString());
    entity.setSourceId(movie.sourceId().id());
    entity.setVersion(movie.version().map(Version::value).orElse(0L));
    entity.setTitle(movie.title().value());
    entity.setReleaseDate(movie.releaseDate().map(ReleaseDate::value).orElse(null));
    entity.setOriginalLanguage(movie.originalLanguage().toString());
    return entity;
  }
  
  public Movie toMovie(MovieEntity entity) {
    return Movie.builder()
        .id(MovieId.of(entity.getId()))
        .publicId(PublicId.of(MediaType.MOVIE, entity.getPublicId()))
        .sourceId(SourceId.of(Source.from(entity.getSource()), entity.getSourceId()))
        .version(Version.of(entity.getVersion()))
        .title(Title.of(entity.getTitle()))
        .releaseDate(ReleaseDate.of(entity.getReleaseDate()))
        .originalLanguage(OriginalLanguage.of(entity.getOriginalLanguage()))
        .build();
  }
}
