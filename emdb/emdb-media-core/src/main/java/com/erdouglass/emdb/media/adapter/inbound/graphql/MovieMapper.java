package com.erdouglass.emdb.media.adapter.inbound.graphql;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.media.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.shared.Version;

@ApplicationScoped
class MovieMapper {

  public MovieView toMovieView(Movie movie) {
    return MovieView.builder()
        .id(movie.publicId().map(MoviePublicId::toString).orElse(null))
        .version(movie.version().map(Version::value).orElse(null))
        .title(movie.title().toString())
        .releaseDate(movie.releaseDate().map(ReleaseDate::toString).orElse(null))
        .originalLanguage(movie.originalLanguage().toString())
        .build();
  }
}
